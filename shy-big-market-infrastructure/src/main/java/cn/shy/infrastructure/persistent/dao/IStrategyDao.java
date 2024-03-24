package cn.shy.infrastructure.persistent.dao;

import cn.shy.domain.strategy.model.entity.StrategyEntity;
import cn.shy.infrastructure.persistent.po.Strategy;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
 * @description 抽奖策略 DAO
 * @create 2023-12-16 13:24
 */
@Mapper
public interface IStrategyDao {

    List<Strategy> queryStrategyList();
    
    Strategy queryStrategyEntityByStrategyId(Long strategyId);
}
