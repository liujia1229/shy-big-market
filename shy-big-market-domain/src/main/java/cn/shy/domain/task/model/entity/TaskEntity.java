package cn.shy.domain.task.model.entity;

import lombok.Data;

/**
 * 任务类实体
 *
 * @author shy
 * @since 2024/4/8 19:35
 */
@Data
public class TaskEntity {
    /**
     * 活动ID
     */
    private String userId;
    /**
     * 消息主题
     */
    private String topic;
    /**
     * 消息编号
     */
    private String messageId;
    /**
     * 消息主体
     */
    private String message;
    
}
