package cn.shy.domain.activity.service.armory;

import cn.shy.domain.activity.model.entity.ActivitySkuEntity;
import cn.shy.domain.activity.repository.IActivityRepository;
import cn.shy.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 活动sku预热，后续库存扣减从缓存中扣除
 *
 * @author shy
 * @since 2024/4/3 14:30
 */
@Service
@Slf4j
public class ActivityArmory implements IActivityArmory, IActivityDispatch {
    
    @Resource
    private IActivityRepository activityRepository;
    
    
    @Override
    public boolean assembleActivitySku(Long sku) {
        //预热sku库存
        ActivitySkuEntity activitySkuEntity = activityRepository.queryActivitySku(sku);
        cacheActivitySkuStockCount(sku, activitySkuEntity.getStockCountSurplus());
        
        //预热活动【查询预热到缓存】
        activityRepository.queryRaffleActivityByActivityId(activitySkuEntity.getActivityId());
        
        //预热活动库存
        activityRepository.queryRaffleActivityCountByActivityCountId(activitySkuEntity.getActivityCountId());
        
        return true;
    }
    
    private void cacheActivitySkuStockCount(Long sku, Integer stockCountSurplus) {
        String cacheKey = Constants.RedisKey.ACTIVITY_SKU_STOCK_COUNT_KEY + sku;
        activityRepository.cacheActivitySkuStockCount(cacheKey, stockCountSurplus);
    }
    
    @Override
    public boolean subtractionActivitySkuStock(Long sku, Date endDateTime) {
        String cacheKey = Constants.RedisKey.ACTIVITY_SKU_STOCK_COUNT_KEY + sku;
        return activityRepository.subtractionActivitySkuStock(sku, cacheKey, endDateTime);
    }
    @Override
    public boolean assembleActivitySkuByActivityId(Long activityId) {
        //查询sku缓存
        List<ActivitySkuEntity> activitySkuEntities = activityRepository.queryActivitySkuListByActivityId(activityId);
        for (ActivitySkuEntity activitySkuEntity : activitySkuEntities) {
            cacheActivitySkuStockCount(activitySkuEntity.getSku(),activitySkuEntity.getStockCountSurplus());
            //预热活动次数【查询时预热到缓存】
            activityRepository.queryRaffleActivityCountByActivityCountId(activitySkuEntity.getActivityCountId());
        }
        //查询活动缓存
        activityRepository.queryRaffleActivityByActivityId(activityId);
        
        return true;
    }
}
