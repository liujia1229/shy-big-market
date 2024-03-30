package cn.shy.infrastructure.persistent.dao;

import cn.shy.infrastructure.persistent.po.StrategyAward;
import cn.shy.infrastructure.persistent.po.StrategyRule;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @description 抽奖策略奖品明细配置 - 概率、规则 DAO
 * @create 2023-12-16 13:24
 */
@Mapper
public interface IStrategyAwardDao {

    List<StrategyAward> queryStrategyAwardList();
    
    String queryStrategyRuleValue(StrategyAward strategyAward);
    
    
    String queryStrategyAwardRuleModels(StrategyAward strategyAward);
    
    void updateStrategyAwardStock(StrategyAward strategyAward);
}
