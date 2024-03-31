package cn.shy.domain.strategy.repository;

import cn.shy.domain.strategy.model.entity.StrategyAwardEntity;
import cn.shy.domain.strategy.model.entity.StrategyEntity;
import cn.shy.domain.strategy.model.entity.StrategyRuleEntity;
import cn.shy.domain.strategy.model.valobj.RuleTreeVO;
import cn.shy.domain.strategy.model.valobj.StrategyAwardRuleModelVO;
import cn.shy.domain.strategy.model.valobj.StrategyAwardStockKeyVO;

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
    
    void storeStrategyAwardSearchRateTable(String key, int rateRange, Map<Integer, Integer> shuffleStrategyAwardSearchRateTable);
    
    /**
     * 返回策略对应的结果范围
     * @param key
     * @return
     */
    int getRateRange(String key);
    
    Integer getStrategyAwardAssemble(String key, int random);
    
    StrategyEntity queryStrategyEntityByStrategyId(Long strategyId);
    
    StrategyRuleEntity queryStrategyRuleEntity(Long strategyId, String ruleWeight);
    
    String queryStrategyRuleValue(Long strategyId, Integer awardId, String ruleModel);
    String queryStrategyRuleValue(Long strategyId,String ruleModel);
    
    StrategyAwardRuleModelVO queryStrategyAwardRuleModelVO(Long strategyId, Integer awardId);
    
    RuleTreeVO queryRuleTreeVOByTreeId(String ruleModels);
    
    /**
     * 缓存商品库存
     * @param cacheKey
     * @param awardCount
     */
    void cacheStrategyAwardCount(String cacheKey, Integer awardCount);
    
    Boolean subtractionAwardStock(String cacheKey);
    
    void awardStockConsumeSendQueue(StrategyAwardStockKeyVO build);
    
    StrategyAwardStockKeyVO takeQueueValue();
    
    void updateStrategyAwardStock(Long strategyId, Integer awardId);
    
    StrategyAwardEntity queryStrategyAwardEntity(Long strategyId, Integer awardId);
    
}
