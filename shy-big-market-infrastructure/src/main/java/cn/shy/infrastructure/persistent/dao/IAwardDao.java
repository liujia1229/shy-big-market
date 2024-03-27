package cn.shy.infrastructure.persistent.dao;

import cn.shy.infrastructure.persistent.po.Award;
import cn.shy.infrastructure.persistent.po.StrategyAward;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @description 奖品表DAO
 * @create 2023-12-16 13:23
 */
@Mapper
public interface IAwardDao {

    List<StrategyAward> queryAwardList();
    
    List<StrategyAward> queryAwardListByStrategyId(Long strategyId);
    
    String queryStrategyAwardRuleModels(StrategyAward strategyAward);
    
}
