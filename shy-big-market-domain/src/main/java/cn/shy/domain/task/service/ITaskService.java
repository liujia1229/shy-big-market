package cn.shy.domain.task.service;

import cn.shy.domain.task.model.entity.TaskEntity;

import java.util.List;

/**
 * 消息任务服务接口
 * @author shy
 * @since 2024/4/8 19:34
 */
public interface ITaskService {
    
    /**
     * 查询前10条未发送mq或者发送失败的task
      * @return
     */
    List<TaskEntity> queryNoSendMessageTaskList();
    
    
    /**
     * 发送mq消息
     * @param taskEntity
     */
    void sendMessage(TaskEntity taskEntity);
    
    /**
     * 更新task任务成功
     * @param userId
     * @param messageId
     */
    void updateTaskSendMessageCompleted(String userId, String messageId);
    
    /**
     * 更新task任务失败
     * @param userId
     * @param messageId
     */
    void updateTaskSendMessageFail(String userId, String messageId);
}
