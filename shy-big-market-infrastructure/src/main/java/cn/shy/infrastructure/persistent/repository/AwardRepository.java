package cn.shy.infrastructure.persistent.repository;

import cn.bugstack.middleware.db.router.annotation.DBRouter;
import cn.bugstack.middleware.db.router.annotation.DBRouterStrategy;
import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import cn.shy.domain.award.event.SendAwardMessageEvent;
import cn.shy.domain.award.model.aggregate.UserAwardRecordAggregate;
import cn.shy.domain.award.model.entity.TaskEntity;
import cn.shy.domain.award.model.entity.UserAwardRecordEntity;
import cn.shy.domain.award.repository.IAwardRepository;
import cn.shy.infrastructure.event.EventPublisher;
import cn.shy.infrastructure.persistent.dao.ITaskDao;
import cn.shy.infrastructure.persistent.dao.IUserAwardRecordDao;
import cn.shy.infrastructure.persistent.po.Task;
import cn.shy.infrastructure.persistent.po.UserAwardRecord;
import cn.shy.types.enums.ResponseCode;
import cn.shy.types.event.BaseEvent;
import cn.shy.types.exception.AppException;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.yaml.snakeyaml.constructor.DuplicateKeyException;

import javax.annotation.Resource;

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
        
        try {
            dbRouter.doRouter(userId);
            transactionTemplate.execute(status -> {
               try{
                   //写入中奖记录
                   userAwardRecordDao.insert(userAwardRecord);
                   //写入任务
                   taskDao.insert(task);
                   return 1;
               } catch (DuplicateKeyException e){
                   status.setRollbackOnly();
                   log.error("写入中奖记录，唯一索引冲突 userId: {} activityId: {} awardId: {}", userId, activityId, awardId, e);
                   throw new AppException(ResponseCode.INDEX_DUP.getCode(), e);
               }
                
            });

        }finally {
            dbRouter.clear();
        }
        //发送mq消息
        try {
            // 发送消息【在事务外执行，如果失败还有任务补偿】
            eventPublisher.publish(task.getTopic(),task.getMessage());
            //更新数据库任务表
            taskDao.updateTaskSendMessageCompleted(task);
        }catch (Exception e){
            log.error("写入中奖记录，发送MQ消息失败 userId: {} topic: {}", userId, task.getTopic());
            //发送失败,更新任务表
            taskDao.updateTaskSendMessageFail(task);
            
        }
    }
}
