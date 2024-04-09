package cn.shy.trigger.job;

import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import cn.shy.domain.task.model.entity.TaskEntity;
import cn.shy.domain.task.service.ITaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 发送MQ消息任务队列
 *
 * @author shy
 * @since 2024/4/8 19:30
 */
@Component
@Slf4j
public class SendMessageTaskJob {
    
    
    @Resource
    private IDBRouterStrategy dbRouter;
    
    @Resource
    private ITaskService taskService;
    
    @Resource
    private ThreadPoolExecutor executor;
    
    
    @Scheduled(cron = "0/5 * * * * ?")
    public void exec() {
        try {
            //获取分库数量
            int dbCount = dbRouter.dbCount();
            //逐个扫描每个库 每个表 task表 只分库 不分表
            for (int dbIdx = 1; dbIdx <= dbCount; dbIdx++) {
                try {
                    int finalDbIdx = dbIdx;
                    executor.execute(() -> {
                        dbRouter.setDBKey(finalDbIdx);
                        dbRouter.setTBKey(0);
                        List<TaskEntity> taskEntities = taskService.queryNoSendMessageTaskList();
                        if (taskEntities.isEmpty()) {
                            return;
                        }
                        for (TaskEntity taskEntity : taskEntities) {
                            executor.execute(() -> {
                                try {
                                    taskService.sendMessage(taskEntity);
                                    taskService.updateTaskSendMessageCompleted(taskEntity.getUserId(), taskEntity.getMessageId());
                                } catch (Exception e) {
                                    log.error("定时任务，发送MQ消息失败 userId: {} topic: {}", taskEntity.getUserId(), taskEntity.getTopic());
                                    taskService.updateTaskSendMessageFail(taskEntity.getUserId(), taskEntity.getMessageId());
                                }
                            });
                        }
                    });
                } finally {
                    dbRouter.clear();
                }
                
            }
            
        } catch (Exception e) {
            log.error("定时任务，扫描MQ任务表发送消息失败。", e);
        } finally {
            dbRouter.clear();
        }
    }
}
