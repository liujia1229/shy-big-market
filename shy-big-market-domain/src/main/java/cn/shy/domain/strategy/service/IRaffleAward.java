package cn.shy.domain.strategy.service;

import cn.shy.domain.strategy.model.entity.StrategyAwardEntity;

import java.util.List;

/**
 * 策略奖品接口
 * @author shy
 * @since 2024/3/30 22:09
 */

public interface IRaffleAward {
    List<StrategyAwardEntity> queryRaffleStrategyAwardList(Long strategyId);
    
    List<StrategyAwardEntity> queryRaffleStrategyAwardListByActivityId(Long activityId);
}
