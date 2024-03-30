package cn.shy.domain.strategy.service;

import cn.shy.domain.strategy.model.valobj.StrategyAwardStockKeyVO;

/**
 * 抽奖库存相关服务，获取库存消耗队列
 * @author shy
 * @since 2024/3/30 15:11
 */
public interface IRaffleStock {
    
    /**
     * 从redis队列中获取奖品库存
     * @return
     */
    StrategyAwardStockKeyVO takeQueueValue();
    
    /**
     * 扣减数据库中对应奖品的库存
     * @param strategyId
     * @param awardId
     */
    void updateStrategyAwardStock(Long strategyId, Integer awardId);
}
