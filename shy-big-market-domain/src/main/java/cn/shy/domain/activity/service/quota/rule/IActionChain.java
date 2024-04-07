package cn.shy.domain.activity.service.quota.rule;

import cn.shy.domain.activity.model.entity.ActivityCountEntity;
import cn.shy.domain.activity.model.entity.ActivityEntity;
import cn.shy.domain.activity.model.entity.ActivitySkuEntity;

/**
 * 下单规则过滤接口
 * @author shy
 * @since 2024/4/1 21:16
 */
public interface IActionChain extends IActionChainArmory{

    boolean action(ActivitySkuEntity activitySkuEntity, ActivityEntity activityEntity, ActivityCountEntity activityCountEntity);
}
