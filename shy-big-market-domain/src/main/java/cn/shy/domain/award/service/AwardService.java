package cn.shy.domain.award.service;

import cn.shy.domain.award.event.SendAwardMessageEvent;
import cn.shy.domain.award.model.aggregate.UserAwardRecordAggregate;
import cn.shy.domain.award.model.entity.TaskEntity;
import cn.shy.domain.award.model.entity.UserAwardRecordEntity;
import cn.shy.domain.award.model.valobj.TaskStateVO;
import cn.shy.domain.award.repository.IAwardRepository;
import cn.shy.types.event.BaseEvent;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 奖品服务接口
 * @author shy
 * @since 2024/4/7 20:49
 */
@Service
public class AwardService implements IAwardService{
    
    
    @Resource
    private IAwardRepository awardRepository;
    
    @Resource
    private SendAwardMessageEvent sendAwardMessageEvent;
    
    
    @Override
    public void saveUserAwardRecord(UserAwardRecordEntity userAwardRecordEntity) {
        //构建消息体对象
        SendAwardMessageEvent.SendAwardMessage sendAwardMessage = SendAwardMessageEvent.SendAwardMessage.builder()
                .awardId(userAwardRecordEntity.getAwardId())
                .awardTitle(userAwardRecordEntity.getAwardTitle())
                .userId(userAwardRecordEntity.getUserId())
                .build();
        
        BaseEvent.EventMessage<SendAwardMessageEvent.SendAwardMessage> sendAwardMessageEventMessage = sendAwardMessageEvent.buildEventMessage(sendAwardMessage);
        
        //构建任务对象
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setMessage(sendAwardMessageEventMessage);
        taskEntity.setTopic(sendAwardMessageEvent.topic());
        taskEntity.setUserId(userAwardRecordEntity.getUserId());
        taskEntity.setMessageId(sendAwardMessageEventMessage.getId());
        taskEntity.setMessage(sendAwardMessageEventMessage);
        taskEntity.setState(TaskStateVO.create);
        
        //构建聚合对象
        UserAwardRecordAggregate userAwardRecordAggregate = UserAwardRecordAggregate.builder()
                .userAwardRecordEntity(userAwardRecordEntity)
                .taskEntity(taskEntity)
                .build();
        
        //存储对象-一个事务，用户的中奖记录
        awardRepository.saveUserAwardRecord(userAwardRecordAggregate);
    }
}
