package cn.shy.domain.credit.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 交易类型枚举值
 *
 * @author shy
 * @since 2024/7/28 2:34
 */
@Getter
@AllArgsConstructor
public enum TradeTypeVO {
    
    FORWARD("forward", "正向交易-积分"),
    REVERSE("reverse", "逆向交易-积分"),
    ;
    
    private final String code;
    private final String info;
    }
