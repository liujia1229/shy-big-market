package cn.shy.infrastructure.persistent.repository;

import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import cn.shy.domain.award.model.aggregate.GiveOutPrizesAggregate;
import cn.shy.domain.award.model.aggregate.UserAwardRecordAggregate;
import cn.shy.domain.award.model.entity.TaskEntity;
import cn.shy.domain.award.model.entity.UserAwardRecordEntity;
import cn.shy.domain.award.model.entity.UserCreditAwardEntity;
import cn.shy.domain.award.repository.IAwardRepository;
import cn.shy.domain.credit.model.valobj.AccountStatusVO;
import cn.shy.infrastructure.event.EventPublisher;
import cn.shy.infrastructure.persistent.dao.*;
import cn.shy.infrastructure.persistent.po.Task;
import cn.shy.infrastructure.persistent.po.UserAwardRecord;
import cn.shy.infrastructure.persistent.po.UserCreditAccount;
import cn.shy.infrastructure.persistent.po.UserRaffleOrder;
import cn.shy.infrastructure.persistent.redis.IRedisService;
import cn.shy.types.common.Constants;
import cn.shy.types.enums.ResponseCode;
import cn.shy.types.exception.AppException;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;
import org.yaml.snakeyaml.constructor.DuplicateKeyException;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author shy
 * @since 2024/4/7 21:06
 */
@Repository
@Slf4j
public class AwardRepository implements IAwardRepository {
    
    @Resource
    private EventPublisher eventPublisher;
    
    @Resource
    private IDBRouterStrategy dbRouter;
    
    @Resource
    private TransactionTemplate transactionTemplate;
    
    @Resource
    private ITaskDao taskDao;
    
    @Resource
    private IUserAwardRecordDao userAwardRecordDao;
    
    @Resource
    private IUserRaffleOrderDao userRaffleOrderDao;
    
    @Resource
    private IAwardDao awardDao;
    
    @Resource
    private IUserCreditAccountDao userCreditAccountDao;
    
    @Resource
    private IRedisService redisService;
    
    
    
    @Override
    public void saveUserAwardRecord(UserAwardRecordAggregate userAwardRecordAggregate) {
        TaskEntity taskEntity = userAwardRecordAggregate.getTaskEntity();
        UserAwardRecordEntity userAwardRecordEntity = userAwardRecordAggregate.getUserAwardRecordEntity();
        String userId = userAwardRecordEntity.getUserId();
        Long activityId = userAwardRecordEntity.getActivityId();
        Integer awardId = userAwardRecordEntity.getAwardId();
        
        
        UserAwardRecord userAwardRecord = new UserAwardRecord();
        userAwardRecord.setUserId(userAwardRecordEntity.getUserId());
        userAwardRecord.setActivityId(userAwardRecordEntity.getActivityId());
        userAwardRecord.setStrategyId(userAwardRecordEntity.getStrategyId());
        userAwardRecord.setOrderId(userAwardRecordEntity.getOrderId());
        userAwardRecord.setAwardId(userAwardRecordEntity.getAwardId());
        userAwardRecord.setAwardTitle(userAwardRecordEntity.getAwardTitle());
        userAwardRecord.setAwardTime(userAwardRecordEntity.getAwardTime());
        userAwardRecord.setAwardState(userAwardRecordEntity.getAwardState().getCode());
        
        Task task = new Task();
        task.setMessage(JSON.toJSONString(taskEntity.getMessage()));
        task.setId(taskEntity.getMessageId());
        task.setTopic(taskEntity.getTopic());
        task.setState(taskEntity.getState().getCode());
        task.setUserId(taskEntity.getUserId());
        task.setMessageId(taskEntity.getMessageId());
        
        UserRaffleOrder userRaffleOrderReq = new UserRaffleOrder();
        userRaffleOrderReq.setOrderId(userAwardRecordEntity.getOrderId());
        userRaffleOrderReq.setUserId(userId);
        
        try {
            dbRouter.doRouter(userId);
            transactionTemplate.execute(status -> {
                try {
                    //写入中奖记录
                    userAwardRecordDao.insert(userAwardRecord);
                    //写入任务
                    taskDao.insert(task);
                    // 更新抽奖单
                    int count = userRaffleOrderDao.updateUserRaffleOrderStateUsed(userRaffleOrderReq);
                    if (1 != count) {
                        status.setRollbackOnly();
                        log.error("写入中奖记录，用户抽奖单已使用过，不可重复抽奖 userId: {} activityId: {} awardId: {}", userId, activityId, awardId);
                        throw new AppException(ResponseCode.ACTIVITY_ORDER_ERROR.getCode(), ResponseCode.ACTIVITY_ORDER_ERROR.getInfo());
                    }
                    return 1;
                } catch (DuplicateKeyException e) {
                    status.setRollbackOnly();
                    log.error("写入中奖记录，唯一索引冲突 userId: {} activityId: {} awardId: {}", userId, activityId, awardId, e);
                    throw new AppException(ResponseCode.INDEX_DUP.getCode(), e);
                }
                
            });
            
        } finally {
            dbRouter.clear();
        }
        //发送mq消息
        try {
            // 发送消息【在事务外执行，如果失败还有任务补偿】
            eventPublisher.publish(task.getTopic(), task.getMessage());
            //更新数据库任务表
            taskDao.updateTaskSendMessageCompleted(task);
        } catch (Exception e) {
            log.error("写入中奖记录，发送MQ消息失败 userId: {} topic: {}", userId, task.getTopic());
            //发送失败,更新任务表
            taskDao.updateTaskSendMessageFail(task);
            
        }
    }
    
    @Override
    public String queryAwardConfig(Integer awardId) {
        return awardDao.queryAwardConfigByAwardId(awardId);
    }
    
    @Override
    public void saveGiveOutPrizesAggregate(GiveOutPrizesAggregate giveOutPrizesAggregate) {
        String userId = giveOutPrizesAggregate.getUserId();
        UserAwardRecordEntity userAwardRecordEntity = giveOutPrizesAggregate.getUserAwardRecordEntity();
        UserCreditAwardEntity userCreditAwardEntity = giveOutPrizesAggregate.getUserCreditAwardEntity();
        
        //构建发奖记录->修改发奖状态
        UserAwardRecord userAwardRecordReq = new UserAwardRecord();
        userAwardRecordReq.setUserId(userAwardRecordEntity.getUserId());
        userAwardRecordReq.setOrderId(userAwardRecordEntity.getOrderId());
        userAwardRecordReq.setAwardState(userAwardRecordEntity.getAwardState().getCode());
        
        //用户积分账户->修改用户积分账户,如果不存在账户则新建
        UserCreditAccount userCreditAccountReq = new UserCreditAccount();
        userCreditAccountReq.setUserId(userCreditAwardEntity.getUserId());
        userCreditAccountReq.setAvailableAmount(userCreditAwardEntity.getCreditAmount());
        userCreditAccountReq.setTotalAmount(userCreditAwardEntity.getCreditAmount());
        userCreditAccountReq.setAccountStatus(AccountStatusVO.open.getCode());
        
        //加锁,防止因为账户不存在->多个线程重新创建->异常
        RLock lock = redisService.getLock(Constants.RedisKey.ACTIVITY_ACCOUNT_LOCK + userId);
        try {
            lock.lock(3, TimeUnit.SECONDS);
            dbRouter.doRouter(userId);
            transactionTemplate.execute((status) -> {
                try {
                    //账户新增/修改
                    UserCreditAccount userCreditAccountRsp = userCreditAccountDao.queryUserCreditAccount(userCreditAccountReq);
                    if (userCreditAccountRsp == null) {
                        userCreditAccountDao.insert(userCreditAccountReq);
                    } else {
                        userCreditAccountDao.updateAddMount(userCreditAccountReq);
                    }
                    
                    //更新中奖记录
                    int i = userAwardRecordDao.updateAwardRecordCompletedState(userAwardRecordReq);
                    if (i == 0) {
                        log.warn("更新中奖记录，重复更新拦截 userId:{} giveOutPrizesAggregate:{}", userId, giveOutPrizesAggregate);
                        status.setRollbackOnly();
                    }
                    return 1;
                }catch (DuplicateKeyException e){
                    status.setRollbackOnly();
                    log.error("更新中奖记录，唯一索引冲突 userId: {} ", userId, e);
                    throw new AppException(ResponseCode.INDEX_DUP.getCode(), e);
                }
            });
        } finally {
            dbRouter.clear();
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
    
    @Override
    public String queryAwardKey(Integer awardId) {
        return awardDao.queryAwardKey(awardId);
    }
}
