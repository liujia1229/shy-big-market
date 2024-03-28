package cn.shy.domain.strategy.service.rule.filter;

import cn.shy.domain.strategy.model.entity.RuleActionEntity;
import cn.shy.domain.strategy.model.entity.RuleMatterEntity;

/**
 * @author shy
 * @since 2024/3/25 21:04
 */
public interface ILogicFilter<T extends RuleActionEntity.RaffleEntity> {
    
    RuleActionEntity<T> filter(RuleMatterEntity ruleMatterEntity);
}
