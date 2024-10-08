package cn.shy.trigger.listener;

import cn.shy.domain.award.event.SendAwardMessageEvent;
import cn.shy.domain.award.model.entity.DistributeAwardEntity;
import cn.shy.domain.award.service.IAwardService;
import cn.shy.types.event.BaseEvent;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 用户奖品记录消息消费者
 *
 * @author shy
 * @since 2024/4/8 19:41
 */
@Slf4j
@Component
public class SendAwardCustomer {
    
    @Value("${spring.rabbitmq.topic.send_award}")
    private String topic;
    
    @Resource
    private IAwardService awardService;
    
    @RabbitListener(queuesToDeclare = @Queue(value = "${spring.rabbitmq.topic.send_award}"))
    public void listener(String message) {
        try {
            log.info("监听用户奖品发送消息 topic: {} message: {}", topic, message);
            BaseEvent.EventMessage<SendAwardMessageEvent.SendAwardMessage> eventMessage =
                    JSON.parseObject(message, new TypeReference<BaseEvent.EventMessage<SendAwardMessageEvent.SendAwardMessage>>() {
                    }.getType());
            SendAwardMessageEvent.SendAwardMessage messageData = eventMessage.getData();
            DistributeAwardEntity distributeAwardEntity = new DistributeAwardEntity();
            distributeAwardEntity.setAwardConfig(messageData.getAwardConfig());
            distributeAwardEntity.setAwardId(messageData.getAwardId());
            distributeAwardEntity.setUserId(messageData.getUserId());
            distributeAwardEntity.setOrderId(messageData.getOrderId());
            //发奖
            awardService.distributeAward(distributeAwardEntity);
        } catch (Exception e) {
            log.error("监听用户奖品发送消息，消费失败 topic: {} message: {}", topic, message);
            throw e;
        }
    }
    
}
