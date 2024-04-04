package cn.shy.domain.activity.service.rule.impl;

import cn.shy.domain.activity.model.entity.ActivityCountEntity;
import cn.shy.domain.activity.model.entity.ActivityEntity;
import cn.shy.domain.activity.model.entity.ActivitySkuEntity;
import cn.shy.domain.activity.model.valobj.ActivitySkuStockKeyVO;
import cn.shy.domain.activity.repository.IActivityRepository;
import cn.shy.domain.activity.service.rule.AbstractActionChain;
import cn.shy.domain.activity.service.rule.armory.IActivityDispatch;
import cn.shy.types.enums.ResponseCode;
import cn.shy.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 商品库存规则节点
 *
 * @author shy
 * @since 2024/4/1 21:20
 */
@Slf4j
@Component("activity_sku_stock_action")
public class ActivitySkuStockActionChain extends AbstractActionChain {
    
    @Resource
    private IActivityRepository activityRepository;
    
    @Resource
    private IActivityDispatch activityDispatch;
    
    @Override
    public boolean action(ActivitySkuEntity activitySkuEntity, ActivityEntity activityEntity, ActivityCountEntity activityCountEntity) {
        log.info("活动责任链-商品库存处理【有效期、状态、库存(sku)】开始。sku:{} activityId:{}", activitySkuEntity.getSku(), activityEntity.getActivityId());
        boolean status = activityDispatch.subtractionActivitySkuStock(activitySkuEntity.getSku(), activityEntity.getEndDateTime());
        if (status) {
            log.info("活动责任链-商品库存处理【有效期、状态、库存(sku)】成功。sku:{} activityId:{}", activitySkuEntity.getSku(), activityEntity.getActivityId());
            // 写入延迟队列，延迟消费更新库存记录
            activityRepository.activitySkuStockConsumeSendQueue(ActivitySkuStockKeyVO.builder()
                    .activityId(activityEntity.getActivityId())
                    .sku(activitySkuEntity.getSku())
                    .build());
            return true;
        }
        throw new AppException(ResponseCode.ACTIVITY_SKU_STOCK_ERROR.getCode(), ResponseCode.ACTIVITY_SKU_STOCK_ERROR.getInfo());
    }
}
