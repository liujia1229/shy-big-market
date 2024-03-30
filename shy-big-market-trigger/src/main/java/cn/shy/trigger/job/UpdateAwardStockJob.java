package cn.shy.trigger.job;

import cn.shy.domain.strategy.model.valobj.StrategyAwardStockKeyVO;
import cn.shy.domain.strategy.service.IRaffleStock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 更新奖品库存任务；为了不让更新库存的压力打到数据库中，这里采用了redis更新缓存库存，异步队列更新数据库，数据库表最终一致即可。
 * @author shy
 * @since 2024/3/30 15:10
 */
@Component
@Slf4j
public class UpdateAwardStockJob {
    
    @Resource
    private IRaffleStock raffleStock;
    
    @Scheduled(cron = "0/20 * * * * ?")
    public void exec(){
        log.info("定时任务，更新奖品消耗库存【延迟队列获取，降低对数据库的更新频次，不要产生竞争】");
        try {
            StrategyAwardStockKeyVO strategyAwardStockKeyVO = raffleStock.takeQueueValue();
            if (strategyAwardStockKeyVO == null){
                return;
            }
            raffleStock.updateStrategyAwardStock(strategyAwardStockKeyVO.getStrategyId(),strategyAwardStockKeyVO.getAwardId());
        } catch (Exception e){
            log.error("定时任务，更新奖品消耗库存失败", e);
        }
    }

}
