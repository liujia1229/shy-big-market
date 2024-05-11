package cn.shy.domain.rebate.service;

import cn.shy.domain.rebate.event.SendRebateMessageEvent;
import cn.shy.domain.rebate.model.aggregate.BehaviorRebateAggregate;
import cn.shy.domain.rebate.model.entity.BehaviorEntity;
import cn.shy.domain.rebate.model.entity.BehaviorRebateOrderEntity;
import cn.shy.domain.rebate.model.entity.TaskEntity;
import cn.shy.domain.rebate.model.valobj.DailyBehaviorRebateVO;
import cn.shy.domain.rebate.model.valobj.TaskStateVO;
import cn.shy.domain.rebate.repository.IBehaviorRebateRepository;
import cn.shy.types.common.Constants;
import cn.shy.types.event.BaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 行为返利服务实现
 *
 * @author shy
 * @since 2024/5/1 21:29
 */
@Slf4j
@Service
public class BehaviorRebateService implements IBehaviorRebateService {
    
    @Resource
    private IBehaviorRebateRepository behaviorRebateRepository;
    
    @Resource
    private SendRebateMessageEvent sendRebateMessageEvent;
    
    @Override
    public List<String> createOrder(BehaviorEntity behaviorEntity) {
        //1.查询返利配置
        List<DailyBehaviorRebateVO> dailyBehaviorRebateVOS = behaviorRebateRepository.queryDailyBehaviorRebateConfig(behaviorEntity.getBehaviorTypeVO());
        if (dailyBehaviorRebateVOS == null || dailyBehaviorRebateVOS.isEmpty()) {
            return new ArrayList<>();
        }
        //2.构建聚合对象
        List<String> orderIds = new ArrayList<>();
        List<BehaviorRebateAggregate> behaviorRebateAggregates = new ArrayList<>();
        for (DailyBehaviorRebateVO dailyBehaviorRebateVO : dailyBehaviorRebateVOS) {
            //拼装业务id,构建order对象
            String bizId = behaviorEntity.getUserId() + Constants.UNDERLINE + dailyBehaviorRebateVO.getRebateType() + Constants.UNDERLINE + behaviorEntity.getOutBusinessNo();
            BehaviorRebateOrderEntity behaviorRebateOrderEntity = BehaviorRebateOrderEntity.builder()
                    .userId(behaviorEntity.getUserId())
                    .orderId(RandomStringUtils.randomNumeric(12))
                    .behaviorType(dailyBehaviorRebateVO.getBehaviorType())
                    .rebateDesc(dailyBehaviorRebateVO.getRebateDesc())
                    .rebateType(dailyBehaviorRebateVO.getRebateType())
                    .rebateConfig(dailyBehaviorRebateVO.getRebateConfig())
                    .outBusinessNo(behaviorEntity.getOutBusinessNo())
                    .bizId(bizId)
                    .build();
            orderIds.add(behaviorRebateOrderEntity.getOrderId());
            
            //mq消息
            SendRebateMessageEvent.RebateMessage rebateMessage = SendRebateMessageEvent.RebateMessage.builder()
                    .userId(behaviorEntity.getUserId())
                    .rebateType(dailyBehaviorRebateVO.getBehaviorType())
                    .rebateConfig(dailyBehaviorRebateVO.getRebateConfig())
                    .bizId(bizId)
                    .build();
            BaseEvent.EventMessage<SendRebateMessageEvent.RebateMessage> rebateMessageEventMessage
                    = sendRebateMessageEvent.buildEventMessage(rebateMessage);
            
            //任务对象
            TaskEntity taskEntity = new TaskEntity();
            taskEntity.setState(TaskStateVO.create);
            taskEntity.setTopic(sendRebateMessageEvent.topic());
            taskEntity.setUserId(behaviorEntity.getUserId());
            taskEntity.setMessageId(rebateMessageEventMessage.getId());
            taskEntity.setMessage(rebateMessageEventMessage);
            
            BehaviorRebateAggregate behaviorRebateAggregate = BehaviorRebateAggregate.builder()
                    .userId(behaviorEntity.getUserId())
                    .behaviorRebateOrderEntity(behaviorRebateOrderEntity)
                    .taskEntity(taskEntity)
                    .build();
            
            behaviorRebateAggregates.add(behaviorRebateAggregate);
        }
        behaviorRebateRepository.saveUserRebateRecord(behaviorEntity.getUserId(), behaviorRebateAggregates);
        //返回订单ids
        return orderIds;
    }
    
    @Override
    public List<BehaviorRebateOrderEntity> queryOrderByOutBusinessNo(String userId, String outBusinessNo) {
        return behaviorRebateRepository.queryOrderByOutBusinessNo(userId,outBusinessNo);
    }
}
