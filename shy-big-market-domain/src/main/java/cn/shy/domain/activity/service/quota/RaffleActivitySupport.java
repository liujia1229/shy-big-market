package cn.shy.domain.activity.service.quota;

import cn.shy.domain.activity.model.entity.ActivityCountEntity;
import cn.shy.domain.activity.model.entity.ActivityEntity;
import cn.shy.domain.activity.model.entity.ActivitySkuEntity;
import cn.shy.domain.activity.repository.IActivityRepository;
import cn.shy.domain.activity.service.quota.rule.factory.DefaultActivityChainFactory;

/**
 * @author shy
 * @since 2024/4/1 21:32
 */
public class RaffleActivitySupport {
    
    protected IActivityRepository activityRepository;

    protected DefaultActivityChainFactory defaultActivityChainFactory;
    
    public RaffleActivitySupport(IActivityRepository activityRepository,DefaultActivityChainFactory defaultActivityChainFactory){
        this.activityRepository = activityRepository;
        this.defaultActivityChainFactory = defaultActivityChainFactory;
    }
    
    public ActivitySkuEntity queryActivitySku(Long sku) {
        return activityRepository.queryActivitySku(sku);
    }
    
    public ActivityEntity queryRaffleActivityByActivityId(Long activityId) {
        return activityRepository.queryRaffleActivityByActivityId(activityId);
    }
    
    public ActivityCountEntity queryRaffleActivityCountByActivityCountId(Long activityCountId) {
        return activityRepository.queryRaffleActivityCountByActivityCountId(activityCountId);
    }
}
