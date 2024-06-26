package cn.shy.trigger.listener;

import cn.shy.domain.activity.service.IRaffleActivitySkuStockService;
import cn.shy.types.event.BaseEvent;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 活动sku库存耗尽
 * @author shy
 * @since 2024/4/3 21:21
 */
@Slf4j
@Component
public class ActivitySkuStockZeroCustomer {
    
    @Value("${spring.rabbitmq.topic.activity_sku_stock_zero}")
    private String topic;
    
    @Resource
    private IRaffleActivitySkuStockService skuStock;
    
    
    @RabbitListener(queuesToDeclare = @Queue("${spring.rabbitmq.topic.activity_sku_stock_zero}"))
    public void listener(String message){
        try {
            log.info("监听活动sku库存消耗为0消息 topic: {} message: {}", topic, message);
            BaseEvent.EventMessage<Long> eventMessage = JSON.parseObject(message, new TypeReference<BaseEvent.EventMessage<Long>>(){
            }.getType());
            Long sku = eventMessage.getData();
            //更新库存
            skuStock.clearActivitySkuStock(sku);
            //清空队列,此时数据库直接清零 不需要再从redis的延迟队列中取出数据更新
            skuStock.clearQueueValue();
        }catch (Exception e){
            log.error("监听活动sku库存消耗为0消息，消费失败 topic: {} message: {}", topic, message);
            throw e;
        }
    }
}
