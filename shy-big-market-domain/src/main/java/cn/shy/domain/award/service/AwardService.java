package cn.shy.domain.award.service;

import cn.shy.domain.award.event.SendAwardMessageEvent;
import cn.shy.domain.award.model.aggregate.UserAwardRecordAggregate;
import cn.shy.domain.award.model.entity.DistributeAwardEntity;
import cn.shy.domain.award.model.entity.TaskEntity;
import cn.shy.domain.award.model.entity.UserAwardRecordEntity;
import cn.shy.domain.award.model.valobj.TaskStateVO;
import cn.shy.domain.award.repository.IAwardRepository;
import cn.shy.domain.award.service.distribute.IDistributeAward;
import cn.shy.types.event.BaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 奖品服务接口
 *
 * @author shy
 * @since 2024/4/7 20:49
 */
@Service
@Slf4j
public class AwardService implements IAwardService {
    
    
    private final IAwardRepository awardRepository;
    
    private final SendAwardMessageEvent sendAwardMessageEvent;
    
    private final Map<String, IDistributeAward> distributeAwardMap;
    
    public AwardService(IAwardRepository awardRepository, SendAwardMessageEvent sendAwardMessageEvent, Map<String, IDistributeAward> distributeAwardMap) {
        this.awardRepository = awardRepository;
        this.sendAwardMessageEvent = sendAwardMessageEvent;
        this.distributeAwardMap = distributeAwardMap;
    }
    
    @Override
    public void saveUserAwardRecord(UserAwardRecordEntity userAwardRecordEntity) {
        //构建消息体对象
        SendAwardMessageEvent.SendAwardMessage sendAwardMessage = SendAwardMessageEvent.SendAwardMessage.builder()
                .awardId(userAwardRecordEntity.getAwardId())
                .awardTitle(userAwardRecordEntity.getAwardTitle())
                .userId(userAwardRecordEntity.getUserId())
                .awardConfig(userAwardRecordEntity.getAwardConfig())
                .orderId(userAwardRecordEntity.getOrderId())
                .build();
        
        BaseEvent.EventMessage<SendAwardMessageEvent.SendAwardMessage> sendAwardMessageEventMessage = sendAwardMessageEvent.buildEventMessage(sendAwardMessage);
        
        //构建任务对象
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setMessage(sendAwardMessageEventMessage);
        taskEntity.setTopic(sendAwardMessageEvent.topic());
        taskEntity.setUserId(userAwardRecordEntity.getUserId());
        taskEntity.setMessageId(sendAwardMessageEventMessage.getId());
        taskEntity.setState(TaskStateVO.create);
        
        //构建聚合对象
        UserAwardRecordAggregate userAwardRecordAggregate = UserAwardRecordAggregate.builder()
                .userAwardRecordEntity(userAwardRecordEntity)
                .taskEntity(taskEntity)
                .build();
        
        //存储对象-一个事务，用户的中奖记录
        awardRepository.saveUserAwardRecord(userAwardRecordAggregate);
    }
    
    @Override
    public void distributeAward(DistributeAwardEntity distributeAwardEntity) {
        //校验发奖
        Integer awardId = distributeAwardEntity.getAwardId();
        String key = awardRepository.queryAwardKey(awardId);
        if (StringUtils.isBlank(key)) {
            log.error("分发奖品,奖品不存在。awardId:{}", awardId);
            return;
        }
        //拿到发奖bean
        IDistributeAward distributeAward = distributeAwardMap.get(key);
        if (distributeAward == null) {
            log.error("分发奖品,对应发放服务不存在。awardId:{},awardKey:{}", awardId, key);
            return;
        }
        //发奖
        distributeAward.giveOutPrizes(distributeAwardEntity);
    }
}
