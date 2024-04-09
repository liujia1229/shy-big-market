package cn.shy.infrastructure.event;

import cn.shy.types.event.BaseEvent;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import sun.plugin2.message.EventMessage;

import javax.annotation.Resource;

/**
 * 消息发送
 * @author shy
 * @since 2024/4/3 15:21
 */
@Component
@Slf4j
public class EventPublisher {

    @Resource
    private RabbitTemplate rabbitTemplate;
   
    public void publish(String topic, BaseEvent.EventMessage<?> eventMessage){
        try {
            String messageJson = JSON.toJSONString(eventMessage);
            rabbitTemplate.convertAndSend(topic,messageJson);
            log.info("发送MQ消息 topic:{} message:{}", topic, messageJson);
        }catch (Exception e){
            log.error("发送MQ消息失败 topic:{} message:{}", topic, JSON.toJSONString(eventMessage), e);
            throw e;
        }
        
    }
    
    public void publish(String topic, String eventMessageJSON) {
        try {
            rabbitTemplate.convertAndSend(topic,eventMessageJSON);
            log.info("发送MQ消息 topic:{} message:{}", topic, eventMessageJSON);
        }catch (Exception e){
            log.error("发送MQ消息失败 topic:{} message:{}", topic, JSON.toJSONString(eventMessageJSON), e);
            throw e;
        }
    }
}
