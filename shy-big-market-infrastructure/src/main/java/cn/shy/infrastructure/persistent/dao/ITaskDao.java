package cn.shy.infrastructure.persistent.dao;

import cn.bugstack.middleware.db.router.annotation.DBRouter;
import cn.shy.domain.task.model.entity.TaskEntity;
import cn.shy.infrastructure.persistent.po.Task;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 任务表，发送MQ
 * @author shy
 * @since 2024/4/4 16:39
 */
@Mapper
public interface ITaskDao {
    void insert(Task task);
    
    @DBRouter
    void updateTaskSendMessageFail(Task task);
    
    @DBRouter
    void updateTaskSendMessageCompleted(Task task);
    
    List<Task> queryNoSendMessageTaskList();
}
