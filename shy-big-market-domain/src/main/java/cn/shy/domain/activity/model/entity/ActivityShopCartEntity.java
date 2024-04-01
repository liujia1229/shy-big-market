package cn.shy.domain.activity.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 购物车实体对象
 *
 * @author shy
 * @since 2024/4/1 15:33
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActivityShopCartEntity {
    /**
     * 商品SKU - activity + activity count
     */
    private Long sku;
    /**
     * 用户id
     */
    private String userId;
}
