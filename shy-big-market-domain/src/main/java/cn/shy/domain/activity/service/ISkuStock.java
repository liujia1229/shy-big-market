package cn.shy.domain.activity.service;

/**
 * 活动sku库存处理接口
 * @author shy
 * @since 2024/4/3 21:26
 */
public interface ISkuStock {
    /**
     * 更新数据库库存
     * @param sku
     */
    void clearActivitySkuStock(Long sku);
    
    /**
     * 清除延时队列中的信息
     */
    void clearQueueValue();
    
    
}
