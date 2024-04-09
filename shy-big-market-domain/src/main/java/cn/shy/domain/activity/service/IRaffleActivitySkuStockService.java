package cn.shy.domain.activity.service;

import cn.shy.domain.activity.model.valobj.ActivitySkuStockKeyVO;

/**
 * 活动sku库存处理接口
 * @author shy
 * @since 2024/4/3 21:26
 */
public interface IRaffleActivitySkuStockService {
    /**
     * 更新数据库库存
     * @param sku
     */
    void clearActivitySkuStock(Long sku);
    
    /**
     * 清除延时队列中的信息
     */
    void clearQueueValue();
    
    
    /**
     * 从队列中取出
     * @return
     */
    ActivitySkuStockKeyVO takeQueueValue();
    
    /**
     * 更新数据库中sku库存
     * @param sku
     */
    void updateActivitySkuStock(Long sku);
}
