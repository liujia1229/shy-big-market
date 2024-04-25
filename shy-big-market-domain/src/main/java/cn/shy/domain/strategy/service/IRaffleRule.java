package cn.shy.domain.strategy.service;

import java.util.Map;

/**
 * 抽奖规则接口；提供对规则的业务功能查询
 * @author shy
 * @since 2024/4/23 22:04
 */
public interface IRaffleRule {

    Map<String,Integer> queryAwardRuleLockCount(String[] treeIds);
}
