package cn.shy.infrastructure.persistent.dao;

import cn.shy.infrastructure.persistent.po.RaffleActivitySku;

/**
 * 商品sku dao
 *
 * @author shy
 * @since 2024/4/1 20:23
 */
public interface IRaffleActivitySkuDao {
    
    RaffleActivitySku queryActivitySku(Long sku);
    
}
