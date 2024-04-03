package cn.shy.domain.activity.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 活动订单实体
 * @author shy
 * @since 2024/4/1 15:29
 */
@AllArgsConstructor
@Getter
public enum OrderStateVO {
    
    completed("completed", "完成");
    
    private final String code;
    private final String desc;
}
