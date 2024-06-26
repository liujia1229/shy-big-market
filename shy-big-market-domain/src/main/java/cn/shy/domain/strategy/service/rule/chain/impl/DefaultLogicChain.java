package cn.shy.domain.strategy.service.rule.chain.impl;

import cn.shy.domain.strategy.service.armory.IStrategyDispatch;
import cn.shy.domain.strategy.service.rule.chain.AbstractLogicChain;
import cn.shy.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 默认的责任链「作为最后一个链」
 * @author shy
 * @since 2024/3/27 22:13
 */
@Slf4j
@Component("default")
public class DefaultLogicChain extends AbstractLogicChain {
    @Override
    protected String ruleModel() {
        return DefaultChainFactory.LogicModel.RULE_DEFAULT.getCode();
    }
    
    @Resource
    private IStrategyDispatch strategyDispatch;
    
    @Override
    public DefaultChainFactory.StrategyAwardVO logic(String userId, Long strategyId) {
        Integer awardId = strategyDispatch.getRandomAwardId(strategyId);
        log.info("抽奖责任链-默认处理 userId: {} strategyId: {} ruleModel: {} awardId: {}", userId, strategyId, ruleModel(), awardId);
        return DefaultChainFactory.StrategyAwardVO.builder()
                .logicModel(this.ruleModel())
                .awardId(awardId)
                .build();
    }
}
