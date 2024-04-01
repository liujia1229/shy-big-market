package cn.shy.domain.activity.repository;

import cn.shy.domain.activity.model.entity.ActivityCountEntity;
import cn.shy.domain.activity.model.entity.ActivityEntity;
import cn.shy.domain.activity.model.entity.ActivitySkuEntity;

/**
 * 活动仓储接口
 * @author shy
 * @since 2024/4/1 15:23
 */
public interface IActivityRepository {
    /**
     * 查询sku
     * @param sku
     * @return
     */
    ActivitySkuEntity queryActivitySku(Long sku);
    
    /**
     * 查询活动实体
     * @param activityId
     * @return
     */
    ActivityEntity queryRaffleActivityByActivityId(Long activityId);
    
    /**
     * 查询活动次数
     * @param activityCountId
     * @return
     */
    ActivityCountEntity queryRaffleActivityCountByActivityCountId(Long activityCountId);
}
