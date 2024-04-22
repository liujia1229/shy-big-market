package cn.shy.domain.activity.service.armory;

/**
 * 活动sku预热
 * @author shy
 * @since 2024/4/3 14:29
 */
public interface IActivityArmory {
    
    /**
     * sku预热
     * @param sku
     * @return
     */
    boolean assembleActivitySku(Long sku);
    
    boolean assembleActivitySkuByActivityId(Long activityId);
}
