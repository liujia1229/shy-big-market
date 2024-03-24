package cn.shy.domain.strategy.service.armory;

/**
 * @author shy
 * @since 2024/3/24 18:30
 */
public interface IStrategyDispatch {
    /**
     * 获取抽奖策略装配的随机结果
     *
     * @param strategyId 策略ID
     * @return 抽奖结果
     */
    Integer getRandomAwardId(Long strategyId);
    
    Integer getRandomAwardId(Long strategyId, String ruleWeightValue);
}
