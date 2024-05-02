package cn.shy.domain.rebate.event;

import cn.shy.types.event.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 发送返利消息事件
 *
 * @author shy
 * @since 2024/5/1 22:09
 */
@Component
public class SendRebateMessageEvent extends BaseEvent<SendRebateMessageEvent.RebateMessage> {
    
    @Value("${spring.rabbitmq.topic.send_rebate}")
    private String topic;
    
    @Override
    public String topic() {
        return this.topic;
    }
    
    @Override
    public EventMessage<RebateMessage> buildEventMessage(RebateMessage data) {
        return EventMessage.<RebateMessage>builder()
                .data(data)
                .id(RandomStringUtils.randomNumeric(11))
                .timestamp(new Date())
                .build();
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RebateMessage {
        
        /**
         * 用户ID
         */
        private String userId;
        /**
         * 返利描述
         */
        private String rebateDesc;
        /**
         * 返利类型
         */
        private String rebateType;
        /**
         * 返利配置
         */
        private String rebateConfig;
        /**
         * 业务ID - 唯一ID，确保幂等
         */
        private String bizId;
    }
    
}
