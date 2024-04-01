package cn.shy.domain.activity.service;

import cn.shy.domain.activity.model.entity.ActivityOrderEntity;
import cn.shy.domain.activity.model.entity.ActivityShopCartEntity;

/**
 * 抽奖活动订单接口
 * @author shy
 * @since 2024/4/1 15:22
 */
public interface IRaffleOrder {
    
    /**
     * 根据sku创建订单获取抽奖次数
     * @param activityShopCartEntity
     * @return
     */
    ActivityOrderEntity createRaffleActivityOrder(ActivityShopCartEntity activityShopCartEntity);
}
