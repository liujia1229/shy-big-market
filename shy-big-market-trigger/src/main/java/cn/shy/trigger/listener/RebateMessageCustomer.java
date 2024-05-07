package cn.shy.trigger.listener;

import cn.shy.domain.activity.model.entity.SkuRechargeEntity;
import cn.shy.domain.activity.service.IRaffleActivityAccountQuotaService;
import cn.shy.domain.rebate.event.SendRebateMessageEvent;
import cn.shy.domain.rebate.model.valobj.BehaviorTypeVO;
import cn.shy.domain.rebate.model.valobj.RebateTypeVO;
import cn.shy.types.enums.ResponseCode;
import cn.shy.types.event.BaseEvent;
import cn.shy.types.exception.AppException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 返利
 *
 * @author shy
 * @since 2024/5/7 12:04
 */
@Slf4j
@Component
public class RebateMessageCustomer {
    
    @Value("${spring.rabbitmq.topic.send_rebate}")
    private String topic;
    
    @Resource
    private IRaffleActivityAccountQuotaService raffleActivityAccountQuotaService;
    
    @RabbitListener(queuesToDeclare = @Queue(value = "${spring.rabbitmq.topic.send_rebate}"))
    public void listener(String message) {
        try {
            BaseEvent.EventMessage<SendRebateMessageEvent.RebateMessage> eventMessage = JSON.parseObject(message,new TypeReference<BaseEvent.EventMessage<SendRebateMessageEvent.RebateMessage>>(){}.getType());
            SendRebateMessageEvent.RebateMessage rebateMessage = eventMessage.getData();
            //非sku奖品暂时不处理
            if (!RebateTypeVO.SKU.getCode().equals(rebateMessage.getRebateType())){
                log.info("监听用户行为返利消息 - 非sku奖励暂时不处理 topic: {} message: {}", topic, message);
                return;
            }
            SkuRechargeEntity skuRechargeEntity = new SkuRechargeEntity();
            skuRechargeEntity.setUserId(rebateMessage.getUserId());
            skuRechargeEntity.setOutBusinessNo(rebateMessage.getBizId());
            skuRechargeEntity.setSku(Long.valueOf(rebateMessage.getRebateConfig()));
            
            raffleActivityAccountQuotaService.createSkuRechargeOrder(skuRechargeEntity);
        } catch (AppException e) {
            if (ResponseCode.INDEX_DUP.getCode().equals(e.getCode())){
                log.warn("监听用户行为返利消息，消费重复 topic: {} message: {}", topic, message, e);
                return;
            }
            throw e;
        } catch (Exception e) {
            log.error("监听用户行为返利消息，消费失败 topic: {} message: {}", topic, message, e);
            throw e;
        }
        
    }
}
