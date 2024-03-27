package cn.shy.domain.strategy.service.rule.impl;

import cn.shy.domain.strategy.model.entity.RuleActionEntity;
import cn.shy.domain.strategy.model.entity.RuleMatterEntity;
import cn.shy.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import cn.shy.domain.strategy.repository.IStrategyRepository;
import cn.shy.domain.strategy.service.annotation.LogicStrategy;
import cn.shy.domain.strategy.service.rule.ILogicFilter;
import cn.shy.domain.strategy.service.rule.factory.DefaultLogicFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * lock过滤器:用户抽奖n次后，对应奖品可解锁抽奖
 * @author shy
 * @since 2024/3/27 21:09
 */
@Slf4j
@Component
@LogicStrategy(logicMode = DefaultLogicFactory.LogicModel.RULE_LOCK)
public class RuleLockLogicFilter implements ILogicFilter<RuleActionEntity.RaffleCenterEntity> {
    
    @Resource
    protected IStrategyRepository strategyRepository;
    
    private Long userRaffleCount = 0L;
    
    @Override
    public RuleActionEntity<RuleActionEntity.RaffleCenterEntity> filter(RuleMatterEntity ruleMatterEntity) {
        log.info("规则过滤-次数锁 userId:{} strategyId:{} ruleModel:{}", ruleMatterEntity.getUserId(), ruleMatterEntity.getStrategyId(), ruleMatterEntity.getRuleModel());
        
        //查询到ruleValue:解锁次数
        String ruleValue = strategyRepository.queryStrategyRuleValue(ruleMatterEntity.getStrategyId(), ruleMatterEntity.getAwardId(),ruleMatterEntity.getRuleModel());
        long raffleCount = Long.parseLong(ruleValue);
        
        if (userRaffleCount >= raffleCount){
            return RuleActionEntity.<RuleActionEntity.RaffleCenterEntity>builder()
                    .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                    .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                    .build();
        }
        
        return RuleActionEntity.<RuleActionEntity.RaffleCenterEntity>builder()
                .code(RuleLogicCheckTypeVO.TAKE_OVER.getCode())
                .info(RuleLogicCheckTypeVO.TAKE_OVER.getInfo())
                .build();
    }
}
