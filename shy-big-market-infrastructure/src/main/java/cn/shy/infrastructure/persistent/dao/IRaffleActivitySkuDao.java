package cn.shy.infrastructure.persistent.dao;

import cn.shy.infrastructure.persistent.po.RaffleActivitySku;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 商品sku dao
 *
 * @author shy
 * @since 2024/4/1 20:23
 */
@Mapper
public interface IRaffleActivitySkuDao {
    
    RaffleActivitySku queryActivitySku(Long sku);
    
    void clearActivitySkuStock(Long sku);
    
    void updateActivitySkuStock(Long sku);
    
    List<RaffleActivitySku> queryActivitySkuListByActivityId(Long activityId);
}
