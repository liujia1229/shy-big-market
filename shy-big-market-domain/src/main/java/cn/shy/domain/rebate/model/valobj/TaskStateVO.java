package cn.shy.domain.rebate.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 任务状态值对象
 * @author shy
 * @since 2024/5/1 21:35
 */
@Getter
@AllArgsConstructor
public enum TaskStateVO {
    
    create("create", "创建"),
    complete("complete", "发送完成"),
    fail("fail", "发送失败"),
    ;
    
    private final String code;
    private final String desc;
}
