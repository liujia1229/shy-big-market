package cn.shy.domain.strategy.service.rule.tree;

import cn.shy.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;

/**
 * 规则数接口
 * @author shy
 * @since 2024/3/28 15:18
 */
public interface ILogicTreeNode {
    DefaultTreeFactory.TreeActionEntity logic(String userId,Long strategyId,Integer awardId,String ruleValue);
}
