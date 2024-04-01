package cn.shy.infrastructure.persistent.repository;

import cn.shy.domain.activity.model.entity.ActivityCountEntity;
import cn.shy.domain.activity.model.entity.ActivityEntity;
import cn.shy.domain.activity.model.entity.ActivitySkuEntity;
import cn.shy.domain.activity.model.valobj.ActivityStateVO;
import cn.shy.domain.activity.repository.IActivityRepository;
import cn.shy.infrastructure.persistent.dao.*;
import cn.shy.infrastructure.persistent.po.RaffleActivity;
import cn.shy.infrastructure.persistent.po.RaffleActivityCount;
import cn.shy.infrastructure.persistent.po.RaffleActivitySku;
import cn.shy.infrastructure.persistent.redis.IRedisService;
import cn.shy.types.common.Constants;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * 活动数据仓储层
 *
 * @author shy
 * @since 2024/4/1 20:17
 */
@Repository
public class ActivityRepository implements IActivityRepository {
    
    @Resource
    private IRaffleActivityDao raffleActivityDao;
    
    @Resource
    private IRaffleActivityOrderDao raffleActivityOrderDao;
    
    @Resource
    private IRaffleActivityCountDao raffleActivityCountDao;
    
    @Resource
    private IRaffleActivityAccountDao raffleActivityAccountDao;
    
    @Resource
    private IRaffleActivitySkuDao raffleActivitySkuDao;
    
    @Resource
    private IRedisService redisService;
    
    
    @Override
    public ActivitySkuEntity queryActivitySku(Long sku) {
        RaffleActivitySku raffleActivitySku = raffleActivitySkuDao.queryActivitySku(sku);
        return ActivitySkuEntity.builder()
                .sku(raffleActivitySku.getSku())
                .activityId(raffleActivitySku.getActivityId())
                .activityCountId(raffleActivitySku.getActivityCountId())
                .stockCount(raffleActivitySku.getStockCount())
                .stockCountSurplus(raffleActivitySku.getStockCountSurplus())
                .build();
    }
    
    @Override
    public ActivityEntity queryRaffleActivityByActivityId(Long activityId) {
        //从缓存中优先获取
        String cacheKey = Constants.RedisKey.ACTIVITY_KEY + activityId;
        ActivityEntity activityEntity = redisService.getValue(cacheKey);
        if (activityEntity != null) {
            return activityEntity;
        }
        RaffleActivity raffleActivity = raffleActivityDao.queryRaffleActivityByActivityId(activityId);
        activityEntity = ActivityEntity.builder()
                .activityId(raffleActivity.getActivityId())
                .activityName(raffleActivity.getActivityName())
                .activityDesc(raffleActivity.getActivityDesc())
                .beginDateTime(raffleActivity.getBeginDateTime())
                .endDateTime(raffleActivity.getEndDateTime())
                .strategyId(raffleActivity.getStrategyId())
                .state(ActivityStateVO.valueOf(raffleActivity.getState()))
                .build();
        redisService.setValue(cacheKey, activityEntity);
        return activityEntity;
    }
    
    @Override
    public ActivityCountEntity queryRaffleActivityCountByActivityCountId(Long activityCountId) {
        // 优先从缓存获取
        String cacheKey = Constants.RedisKey.ACTIVITY_COUNT_KEY + activityCountId;
        ActivityCountEntity activityCountEntity = redisService.getValue(cacheKey);
        // 从库中获取数据
        if (null != activityCountEntity) {
            return activityCountEntity;
        }
        RaffleActivityCount raffleActivityCount = raffleActivityCountDao.queryRaffleActivityCountByActivityCountId(activityCountId);
        activityCountEntity = ActivityCountEntity.builder()
                .activityCountId(raffleActivityCount.getActivityCountId())
                .totalCount(raffleActivityCount.getTotalCount())
                .dayCount(raffleActivityCount.getDayCount())
                .monthCount(raffleActivityCount.getMonthCount())
                .build();
        redisService.setValue(cacheKey, activityCountEntity);
        return activityCountEntity;
    }
}
