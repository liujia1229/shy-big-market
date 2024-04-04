package cn.shy.test;

import cn.shy.domain.activity.event.ActivitySkuStockZeroMessageEvent;
import cn.shy.infrastructure.event.EventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author shy
 * @since 2024/4/3 15:25
 */

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class RabbitMQTest {

    @Resource
    private EventPublisher eventPublisher;

    
    @Resource
    private ActivitySkuStockZeroMessageEvent activitySkuStockZeroMessageEvent;
    
    @Test
    public void testSend(){
        eventPublisher.publish(activitySkuStockZeroMessageEvent.topic(),activitySkuStockZeroMessageEvent.buildEventMessage(123L));
    }
}
