package cn.shy.trigger.job;

import cn.shy.domain.activity.model.valobj.ActivitySkuStockKeyVO;
import cn.shy.domain.activity.service.IRaffleActivitySkuStockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * sku库存更新，库存扣减之后写入延时队列，定时任务遍历队列 更新数据库库存
 * @author shy
 * @since 2024/4/7 22:08
 */
@Component
@Slf4j
public class UpdateActivitySkuStockJob {

    @Resource
    private IRaffleActivitySkuStockService skuStock;
    
    @Scheduled(cron = "0/5 * * * * ?")
    public void exec(){
        try {
            ActivitySkuStockKeyVO activitySkuStockKeyVO = skuStock.takeQueueValue();
            if (activitySkuStockKeyVO == null){
                return;
            }
            log.info("定时任务，更新活动sku库存 sku:{} activityId:{}", activitySkuStockKeyVO.getSku(), activitySkuStockKeyVO.getActivityId());
            skuStock.updateActivitySkuStock(activitySkuStockKeyVO.getSku());
        }catch (Exception e){
            log.error("定时任务，更新活动sku库存失败", e);
        }
    
    }
}
