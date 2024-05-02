package cn.shy.infrastructure.persistent.repository;

import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import cn.shy.domain.rebate.model.aggregate.BehaviorRebateAggregate;
import cn.shy.domain.rebate.model.entity.BehaviorRebateOrderEntity;
import cn.shy.domain.rebate.model.entity.TaskEntity;
import cn.shy.domain.rebate.model.valobj.BehaviorTypeVO;
import cn.shy.domain.rebate.model.valobj.DailyBehaviorRebateVO;
import cn.shy.domain.rebate.repository.IBehaviorRebateRepository;
import cn.shy.infrastructure.event.EventPublisher;
import cn.shy.infrastructure.persistent.dao.IDailyBehaviorRebateDao;
import cn.shy.infrastructure.persistent.dao.ITaskDao;
import cn.shy.infrastructure.persistent.dao.IUserBehaviorRebateOrderDao;
import cn.shy.infrastructure.persistent.po.DailyBehaviorRebate;
import cn.shy.infrastructure.persistent.po.Task;
import cn.shy.infrastructure.persistent.po.UserBehaviorRebateOrder;
import cn.shy.types.enums.ResponseCode;
import cn.shy.types.exception.AppException;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.yaml.snakeyaml.constructor.DuplicateKeyException;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 行为返利数据仓储层实现
 *
 * @author shy
 * @since 2024/5/2 13:57
 */
@Repository
@Slf4j
public class BehaviorRebateRepository implements IBehaviorRebateRepository {
    
    @Resource
    private IDailyBehaviorRebateDao dailyBehaviorRebateDao;
    
    @Resource
    private IUserBehaviorRebateOrderDao userBehaviorRebateOrderDao;
    
    @Resource
    private IDBRouterStrategy dbRouter;
    
    @Resource
    private TransactionTemplate transactionTemplate;
    
    @Resource
    private ITaskDao taskDao;
    
    @Resource
    private EventPublisher eventPublisher;
    
    @Override
    public List<DailyBehaviorRebateVO> queryDailyBehaviorRebateConfig(BehaviorTypeVO behaviorTypeVO) {
        List<DailyBehaviorRebate> dailyBehaviorRebates
                = dailyBehaviorRebateDao.queryDailyBehaviorRebateByBehaviorType(behaviorTypeVO.getCode());
        List<DailyBehaviorRebateVO> dailyBehaviorRebateVOS = new ArrayList<>(dailyBehaviorRebates.size());
        for (DailyBehaviorRebate dailyBehaviorRebate : dailyBehaviorRebates) {
            
            dailyBehaviorRebateVOS.add(DailyBehaviorRebateVO.builder()
                    .behaviorType(dailyBehaviorRebate.getBehaviorType())
                    .rebateDesc(dailyBehaviorRebate.getRebateDesc())
                    .rebateType(dailyBehaviorRebate.getRebateType())
                    .rebateConfig(dailyBehaviorRebate.getRebateConfig())
                    .build());
        }
        return dailyBehaviorRebateVOS;
    }
    
    @Override
    public void saveUserRebateRecord(String userId, List<BehaviorRebateAggregate> behaviorRebateAggregates) {
        try {
            dbRouter.doRouter(userId);
            
            transactionTemplate.execute(status -> {
                try {
                    for (BehaviorRebateAggregate behaviorRebateAggregate : behaviorRebateAggregates) {
                        BehaviorRebateOrderEntity behaviorRebateOrderEntity = behaviorRebateAggregate.getBehaviorRebateOrderEntity();
                        // 用户行为返利订单对象
                        UserBehaviorRebateOrder userBehaviorRebateOrder = new UserBehaviorRebateOrder();
                        userBehaviorRebateOrder.setUserId(behaviorRebateOrderEntity.getUserId());
                        userBehaviorRebateOrder.setOrderId(behaviorRebateOrderEntity.getOrderId());
                        userBehaviorRebateOrder.setBehaviorType(behaviorRebateOrderEntity.getBehaviorType());
                        userBehaviorRebateOrder.setRebateDesc(behaviorRebateOrderEntity.getRebateDesc());
                        userBehaviorRebateOrder.setRebateType(behaviorRebateOrderEntity.getRebateType());
                        userBehaviorRebateOrder.setRebateConfig(behaviorRebateOrderEntity.getRebateConfig());
                        userBehaviorRebateOrder.setBizId(behaviorRebateOrderEntity.getBizId());
                        userBehaviorRebateOrderDao.insert(userBehaviorRebateOrder);
                        
                        TaskEntity taskEntity = behaviorRebateAggregate.getTaskEntity();
                        Task task = new Task();
                        task.setUserId(taskEntity.getUserId());
                        task.setTopic(taskEntity.getTopic());
                        task.setMessageId(taskEntity.getMessageId());
                        task.setMessage(JSON.toJSONString(taskEntity.getMessage()));
                        task.setState(taskEntity.getState().getCode());
                        taskDao.insert(task);
                    }
                    return 1;
                } catch (DuplicateKeyException e) {
                    status.setRollbackOnly();
                    log.error("写入返利记录，唯一索引冲突 userId: {}", userId, e);
                    throw new AppException(ResponseCode.INDEX_DUP.getCode(), e);
                }
            });
        } finally {
            dbRouter.clear();
        }
        
        //发送mq消息
        for (BehaviorRebateAggregate behaviorRebateAggregate : behaviorRebateAggregates) {
            TaskEntity taskEntity = behaviorRebateAggregate.getTaskEntity();
            Task task = new Task();
            task.setMessageId(taskEntity.getMessageId());
            task.setUserId(taskEntity.getUserId());
            try {
                eventPublisher.publish(taskEntity.getTopic(), taskEntity.getMessage());
                taskDao.updateTaskSendMessageCompleted(task);
            } catch (Exception e) {
                log.error("写入返利记录，发送MQ消息失败 userId: {} topic: {}", userId, task.getTopic());
                taskDao.updateTaskSendMessageFail(task);
            }
        }
        
    }
}
