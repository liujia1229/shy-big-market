package cn.shy.domain.strategy.service.rule.chain.impl;

import cn.shy.domain.strategy.service.armory.IStrategyDispatch;
import cn.shy.domain.strategy.service.rule.chain.AbstractLogicChain;
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
        return "default";
    }
    
    @Resource
    private IStrategyDispatch strategyDispatch;
    
    @Override
    public Integer logic(String userId, Long strategyId) {
        Integer awardId = strategyDispatch.getRandomAwardId(strategyId);
        log.info("抽奖责任链-默认处理 userId: {} strategyId: {} ruleModel: {} awardId: {}", userId, strategyId, ruleModel(), awardId);
        return awardId;
    }
}
