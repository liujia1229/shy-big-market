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
    
    /**
     * 根据活动id 缓存 活动信息、对应的sku信息、sku库存信息
     * @param activityId
     * @return
     */
    boolean assembleActivitySkuByActivityId(Long activityId);
}
