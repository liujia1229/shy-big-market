package cn.shy.domain.activity.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 活动状态枚举
 * @author shy
 * @since 2024/4/1 15:29
 */
@Getter
@AllArgsConstructor
public enum ActivityStateVO {
    create("create", "创建"),
    open("open", "开启"),
    close("close", "关闭"),
    ;
    
    private final String code;
    private final String desc;
}
