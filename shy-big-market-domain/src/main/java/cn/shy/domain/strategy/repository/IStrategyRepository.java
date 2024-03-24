package cn.shy.domain.strategy.repository;

import cn.shy.domain.strategy.model.entity.StrategyAwardEntity;

import java.util.List;
import java.util.Map;

/**
 * 策略服务仓储接口
 * @author shy
 * @since 2024/3/23 22:18
 */
public interface IStrategyRepository {
    /**
     * 查询策略配置
     * @param strategyId
     * @return
     */
    List<StrategyAwardEntity> queryStrategyAwardList(Long strategyId);
    
    void storeStrategyAwardSearchRateTable(Long strategyId, int rateRange, Map<Integer, Integer> shuffleStrategyAwardSearchRateTable);
    
    /**
     * 返回策略对应的结果范围
     * @param strategyId
     * @return
     */
    int getRateRange(Long strategyId);
    
    Integer getStrategyAwardAssemble(Long strategyId, int random);
}
