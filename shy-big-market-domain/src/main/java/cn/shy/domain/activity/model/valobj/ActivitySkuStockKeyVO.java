package cn.shy.domain.activity.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 活动sku库存 key 值对象
 * @author shy
 * @since 2024/4/3 21:14
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActivitySkuStockKeyVO {
    
    /** 商品sku */
    private Long sku;
    /** 活动ID */
    private Long activityId;
    
}
