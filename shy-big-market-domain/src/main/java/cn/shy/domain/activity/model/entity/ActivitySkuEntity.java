package cn.shy.domain.activity.model.entity;

import cn.shy.domain.activity.model.valobj.ActivityStateVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 活动sku实体
 *
 * @author shy
 * @since 2024/4/1 15:27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActivitySkuEntity {
    /**
     * 商品sku
     */
    private Long sku;
    /**
     * 活动ID
     */
    private Long activityId;
    /**
     * 活动个人参数ID；在这个活动上，一个人可参与多少次活动（总、日、月）
     */
    private Long activityCountId;
    /**
     * 库存总量
     */
    private Integer stockCount;
    /**
     * 剩余库存
     */
    private Integer stockCountSurplus;
}
