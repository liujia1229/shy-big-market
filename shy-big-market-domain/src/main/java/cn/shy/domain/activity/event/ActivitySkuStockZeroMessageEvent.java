package cn.shy.domain.activity.event;

import cn.shy.types.event.BaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author shy
 * @since 2024/4/3 19:56
 */
@Component
@Slf4j
public class ActivitySkuStockZeroMessageEvent extends BaseEvent<Long> {
    
    @Value("${spring.rabbitmq.topic.activity_sku_stock_zero}")
    private String topic;
    
    @Override
    public String topic() {
        return topic;
    }
    
    @Override
    public EventMessage<Long> buildEventMessage(Long data) {
        return EventMessage.<Long>builder()
                .id(RandomStringUtils.randomNumeric(11))
                .data(data)
                .timestamp(new Date())
                .build();
    }
}
