package cn.shy.domain.rebate.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author shy
 * @since 2024/5/7 15:44
 */
@Getter
@AllArgsConstructor
public enum RebateTypeVO {
    
    SKU("sku", "活动库存充值商品"),
    INTEGRAL("integral", "用户活动积分"),
    ;
    
    private final String code;
    private final String info;
}
