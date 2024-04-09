package cn.shy.domain.task.repository;

import cn.shy.domain.task.model.entity.TaskEntity;

import java.util.List;

/**
 * 任务服务仓储接口
 * @author shy
 * @since 2024/4/8 19:35
 */
public interface ITaskRepository {
    List<TaskEntity> queryNoSendMessageTaskList();
    
    void sendMessage(TaskEntity taskEntity);
    
    void updateTaskSendMessageCompleted(String userId, String messageId);
    
    void updateTaskSendMessageFail(String userId, String messageId);
}
