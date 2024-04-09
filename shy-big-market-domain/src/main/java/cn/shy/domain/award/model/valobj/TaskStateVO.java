package cn.shy.domain.award.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * 任务状态值对象
 * @author shy
 * @since 2024/4/7 21:05
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
