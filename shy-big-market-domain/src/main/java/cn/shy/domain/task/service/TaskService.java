package cn.shy.domain.task.service;

import cn.shy.domain.task.model.entity.TaskEntity;
import cn.shy.domain.task.repository.ITaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 消息任务服务
 * @author shy
 * @since 2024/4/8 19:34
 */
@Service
@Slf4j
public class TaskService implements ITaskService{
    
    @Resource
    private ITaskRepository taskRepository;
    
    
    @Override
    public List<TaskEntity> queryNoSendMessageTaskList() {
        return taskRepository.queryNoSendMessageTaskList();
    }
    
    @Override
    public void sendMessage(TaskEntity taskEntity) {
        taskRepository.sendMessage(taskEntity);
    }
    
    @Override
    public void updateTaskSendMessageCompleted(String userId, String messageId) {
        taskRepository.updateTaskSendMessageCompleted(userId,messageId);
    }
    
    @Override
    public void updateTaskSendMessageFail(String userId, String messageId) {
        taskRepository.updateTaskSendMessageFail(userId,messageId);
    }
}
