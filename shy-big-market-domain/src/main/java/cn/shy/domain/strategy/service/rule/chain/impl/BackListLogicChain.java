package cn.shy.domain.strategy.service.rule.chain.impl;

import cn.shy.domain.strategy.repository.IStrategyRepository;
import cn.shy.domain.strategy.service.rule.chain.AbstractLogicChain;
import cn.shy.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import cn.shy.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 黑名单责任链
 * @author shy
 * @since 2024/3/27 22:13
 */
@Slf4j
@Component("rule_blacklist")
public class BackListLogicChain extends AbstractLogicChain {
    @Override
    protected String ruleModel() {
        return DefaultChainFactory.LogicModel.RULE_BLACKLIST.getCode();
    }
    
    @Resource
    private IStrategyRepository strategyRepository;
    
    @Override
    public DefaultChainFactory.StrategyAwardVO logic(String userId, Long strategyId) {
        //查数据库规则配置
        String ruleValue = strategyRepository.
                queryStrategyRuleValue(strategyId, this.ruleModel());
        
        String[] splitRuleValue = ruleValue.split(Constants.COLON);
        Integer awardId = Integer.parseInt(splitRuleValue[0]);
        
        //判断是否黑名单用户
        String[] userBlackIds = splitRuleValue[1].split(Constants.SPLIT);
        for (String userBlackId : userBlackIds) {
            if (userBlackId.equals(userId)){
                log.info("抽奖责任链-黑名单接管 userId: {} strategyId: {} ruleModel: {} awardId: {}", userId, strategyId, ruleModel(), awardId);
                return DefaultChainFactory.StrategyAwardVO.builder()
                        .awardId(awardId)
                        .logicModel(this.ruleModel())
                        .build();
            }
        }
        
        return this.next().logic(userId,strategyId);
    }
}
