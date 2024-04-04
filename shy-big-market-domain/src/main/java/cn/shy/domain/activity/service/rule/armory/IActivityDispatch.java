package cn.shy.domain.activity.service.rule.armory;

import java.util.Date;

/**
 * 活动调度【扣减库存】
 * @author shy
 * @since 2024/4/3 15:08
 */
public interface IActivityDispatch {
    
    /**
     * 扣减sku库存
     * @param sku
     * @param endDateTime
     * @return
     */
    boolean subtractionActivitySkuStock(Long sku, Date endDateTime);
}
