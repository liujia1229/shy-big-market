package cn.shy.domain.strategy.service.raffle;

import cn.shy.domain.strategy.model.entity.StrategyAwardEntity;
import cn.shy.domain.strategy.model.valobj.RuleTreeVO;
import cn.shy.domain.strategy.model.valobj.StrategyAwardRuleModelVO;
import cn.shy.domain.strategy.model.valobj.StrategyAwardStockKeyVO;
import cn.shy.domain.strategy.service.AbstractRaffleStrategy;
import cn.shy.domain.strategy.service.IRaffleAward;
import cn.shy.domain.strategy.service.IRaffleRule;
import cn.shy.domain.strategy.service.IRaffleStock;
import cn.shy.domain.strategy.service.rule.chain.ILogicChain;
import cn.shy.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import cn.shy.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import cn.shy.domain.strategy.service.rule.tree.factory.engine.IDecisionTreeEngine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 默认的抽奖策略实现
 *
 * @author shy
 * @since 2024/3/25 21:58
 */
@Slf4j
@Service
public class DefaultRaffleStrategy extends AbstractRaffleStrategy implements IRaffleStock, IRaffleAward, IRaffleRule {
    
    
    @Override
    public DefaultChainFactory.StrategyAwardVO raffleLogicChain(String userId, Long strategyId) {
        ILogicChain logicChain = defaultChainFactory.openLogicChain(strategyId);
        return logicChain.logic(userId, strategyId);
    }
    
    @Override
    public void updateStrategyAwardStock(Long strategyId, Integer awardId) {
        strategyRepository.updateStrategyAwardStock(strategyId, awardId);
    }
    
    @Override
    public DefaultTreeFactory.StrategyAwardVO raffleLogicTree(String userId, Long strategyId, Integer awardId, Date endDateTime) {
        StrategyAwardRuleModelVO strategyAwardRuleModelVO = strategyRepository.queryStrategyAwardRuleModelVO(strategyId, awardId);
        if (strategyAwardRuleModelVO == null) {
            return DefaultTreeFactory.StrategyAwardVO.builder()
                    .awardId(awardId)
                    .build();
        }
        RuleTreeVO ruleTreeVO = strategyRepository.queryRuleTreeVOByTreeId(strategyAwardRuleModelVO.getRuleModels());
        if (ruleTreeVO == null) {
            throw new RuntimeException("存在抽奖策略配置的规则模型 Key，未在库表 rule_tree、rule_tree_node、rule_tree_line 配置对应的规则树信息 " + strategyAwardRuleModelVO.getRuleModels());
        }
        IDecisionTreeEngine decisionTreeEngine = defaultTreeFactory.openLogicTree(ruleTreeVO);
        
        return decisionTreeEngine.process(userId, strategyId, awardId, endDateTime);
    }
    
    @Override
    public StrategyAwardStockKeyVO takeQueueValue() {
        return strategyRepository.takeQueueValue();
    }
    
    @Override
    public List<StrategyAwardEntity> queryRaffleStrategyAwardList(Long strategyId) {
        return strategyRepository.queryStrategyAwardList(strategyId);
    }
    
    @Override
    public List<StrategyAwardEntity> queryRaffleStrategyAwardListByActivityId(Long activityId) {
        Long strategyId = strategyRepository.queryStrategyIdByActivityId(activityId);
        return this.queryRaffleStrategyAwardList(strategyId);
    }
    
    @Override
    public Map<String, Integer> queryAwardRuleLockCount(String[] treeIds) {
        return strategyRepository.queryAwardRuleLockCount(treeIds);
    }
}
