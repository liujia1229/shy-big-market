package cn.shy.domain.strategy.service.raffle;

import cn.shy.domain.strategy.model.entity.RaffleFactorEntity;
import cn.shy.domain.strategy.model.entity.RuleActionEntity;
import cn.shy.domain.strategy.model.entity.RuleMatterEntity;
import cn.shy.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import cn.shy.domain.strategy.model.valobj.RuleTreeVO;
import cn.shy.domain.strategy.model.valobj.StrategyAwardRuleModelVO;
import cn.shy.domain.strategy.repository.IStrategyRepository;
import cn.shy.domain.strategy.service.AbstractRaffleStrategy;
import cn.shy.domain.strategy.service.rule.chain.ILogicChain;
import cn.shy.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import cn.shy.domain.strategy.service.rule.filter.ILogicFilter;
import cn.shy.domain.strategy.service.rule.filter.factory.DefaultLogicFactory;
import cn.shy.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import cn.shy.domain.strategy.service.rule.tree.factory.engine.IDecisionTreeEngine;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 默认的抽奖策略实现
 *
 * @author shy
 * @since 2024/3/25 21:58
 */
@Slf4j
@Service
public class DefaultRaffleStrategy extends AbstractRaffleStrategy {
    
    @Resource
    private DefaultLogicFactory logicFactory;
    
    @Resource
    private DefaultChainFactory defaultChainFactory;
    
    @Resource
    private DefaultTreeFactory defaultTreeFactory;
    
    @Resource
    private IStrategyRepository strategyRepository;
    
    @Override
    public DefaultChainFactory.StrategyAwardVO raffleLogicChain(String userId, Long strategyId) {
        ILogicChain logicChain = defaultChainFactory.openLogicChain(strategyId);
        return logicChain.logic(userId, strategyId);
    }
    
    @Override
    public DefaultTreeFactory.StrategyAwardVO raffleLogicTree(String userId, Long strategyId, Integer awardId) {
        StrategyAwardRuleModelVO strategyAwardRuleModelVO = strategyRepository.queryStrategyAwardRuleModelVO(strategyId, awardId);
        if (strategyAwardRuleModelVO == null){
            return DefaultTreeFactory.StrategyAwardVO.builder()
                    .awardId(awardId)
                    .build();
        }
        RuleTreeVO ruleTreeVO = strategyRepository.queryRuleTreeVOByTreeId(strategyAwardRuleModelVO.getRuleModels());
        IDecisionTreeEngine decisionTreeEngine = defaultTreeFactory.openLogicTree(ruleTreeVO);
        
        return decisionTreeEngine.process(userId, strategyId, awardId);
    }
    
}
