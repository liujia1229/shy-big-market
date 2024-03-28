package cn.shy.domain.strategy.service.rule.tree.factory.engine;

import cn.shy.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;

/**
 * 规则树组合接口
 * @author shy
 * @since 2024/3/28 20:19
 */
public interface IDecisionTreeEngine {
    DefaultTreeFactory.StrategyAwardData process(String userId,Long strategyId,Integer awardId);
}
