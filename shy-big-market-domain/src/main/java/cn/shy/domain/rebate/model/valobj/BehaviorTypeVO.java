package cn.shy.domain.rebate.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 行为类型枚举对象
 *
 * @author shy
 * @since 2024/5/1 21:35
 */
@Getter
@AllArgsConstructor
public enum BehaviorTypeVO {
    
    SIGN("sign", "签到（日历）"),
    OPENAI_PAY("openai_pay", "openai 外部支付完成"),
    ;
    
    private final String code;
    private final String info;
}
