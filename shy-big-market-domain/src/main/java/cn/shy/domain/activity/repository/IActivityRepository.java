package cn.shy.domain.activity.repository;

import cn.shy.domain.activity.model.aggregate.CreateOrderAggregate;
import cn.shy.domain.activity.model.aggregate.CreatePartakeOrderAggregate;
import cn.shy.domain.activity.model.entity.*;
import cn.shy.domain.activity.model.valobj.ActivitySkuStockKeyVO;

import java.util.Date;

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
    
    /**
     * 保存订单
     * @param createOrderAggregate
     */
    void doSaveOrder(CreateOrderAggregate createOrderAggregate);
    
    /**
     * 缓存sku库存
     * @param cacheKey
     * @param stockCountSurplus
     */
    void cacheActivitySkuStockCount(String cacheKey, Integer stockCountSurplus);
    
    /**
     * 扣减redis中的sku库存
     *
     * @param sku
     * @param cacheKey
     * @param endDateTime
     * @return
     */
    boolean subtractionActivitySkuStock(Long sku, String cacheKey, Date endDateTime);
    
    /**
     * 发送消息到延迟队列
     * @param activitySkuStockKeyVO
     */
    void activitySkuStockConsumeSendQueue(ActivitySkuStockKeyVO activitySkuStockKeyVO);
    
    /**
     * 清除活动库存
     * @param sku
     */
    void clearActivitySkuStock(Long sku);
    
    void clearQueueValue();
    
    /**
     * 用户参与订单
     * @param partakeRaffleActivityEntity
     * @return
     */
    UserRaffleOrderEntity queryNoUsedRaffleOrder(PartakeRaffleActivityEntity partakeRaffleActivityEntity);
    
    /**
     * 用户活动总额度查询
     * @param userId
     * @param activityId
     * @return
     */
    ActivityAccountEntity queryActivityAccountByUserId(String userId, Long activityId);
    
    /**
     * 月额度表查询
     * @param userId
     * @param activityId
     * @param month
     * @return
     */
    ActivityAccountMonthEntity queryActivityAccountMonthByUserId(String userId, Long activityId, String month);
    
    /**
     * 日额度表查询
     * @param userId
     * @param activityId
     * @param day
     * @return
     */
    ActivityAccountDayEntity queryActivityAccountDayByUserId(String userId, Long activityId, String day);
    
    /**
     * 保存领取活动聚合对象
     * @param createPartakeOrderAggregate
     */
    void saveCreatePartakeOrderAggregate(CreatePartakeOrderAggregate createPartakeOrderAggregate);
    
    /**
     * 取出队列中的sku
     * @return
     */
    ActivitySkuStockKeyVO takeQueueValue();
    
    /**
     * 更新sku库存
     * @param sku
     */
    void updateActivitySkuStock(Long sku);
    
}
