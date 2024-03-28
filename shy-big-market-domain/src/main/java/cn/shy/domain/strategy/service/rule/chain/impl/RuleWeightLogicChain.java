package cn.shy.domain.strategy.service.rule.chain.impl;

import cn.shy.domain.strategy.repository.IStrategyRepository;
import cn.shy.domain.strategy.service.armory.IStrategyDispatch;
import cn.shy.domain.strategy.service.rule.chain.AbstractLogicChain;
import cn.shy.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * 权重抽奖责任链
 *
 * @author shy
 * @since 2024/3/27 22:13
 */
@Slf4j
@Component("rule_weight")
public class RuleWeightLogicChain extends AbstractLogicChain {
    @Override
    protected String ruleModel() {
        return "rule_weight";
    }
    
    @Resource
    private IStrategyRepository strategyRepository;
    
    /**
     * 用户积分，当前写死，通过反射修改
     */
    public Long userScore = 0L;
    
    @Resource
    private IStrategyDispatch strategyDispatch;
    
    
    @Override
    public Integer logic(String userId, Long strategyId) {
        log.info("抽奖责任链-权重开始 userId: {} strategyId: {} ruleModel: {}", userId, strategyId, ruleModel());
        String ruleValue = strategyRepository.queryStrategyRuleValue(strategyId, this.ruleModel());
        Map<Long, String> analyticalValueGroup = getAnalyticalValue(ruleValue);
        
        //转换keys
        List<Long> analyticalSortedKeys  = new ArrayList<>(analyticalValueGroup.keySet());
        Collections.sort(analyticalSortedKeys);
        
        Long nextValue = analyticalSortedKeys.stream()
                .sorted(Comparator.reverseOrder())
                .filter(key -> userScore >= key)
                .findFirst()
                .orElse(null);
        
        if (null != nextValue){
            Integer awardId = strategyDispatch.getRandomAwardId(strategyId, analyticalValueGroup.get(nextValue));
            log.info("抽奖责任链-权重接管 userId: {} strategyId: {} ruleModel: {} awardId: {}", userId, strategyId, ruleModel(), awardId);
            return awardId;
        }
        // 5. 过滤其他责任链
        log.info("抽奖责任链-权重放行 userId: {} strategyId: {} ruleModel: {}", userId, strategyId, ruleModel());
        return this.next().logic(userId, strategyId);
    }
    
    private Map<Long, String> getAnalyticalValue(String ruleValue) {
        String[] ruleValueGroups = ruleValue.split(Constants.SPACE);
        Map<Long,String> ruleValueMap = new HashMap<>(ruleValueGroups.length);
        
        for (String ruleValueGroup : ruleValueGroups) {
            if (ruleValueGroup == null || "".equals(ruleValueGroup)){
                return ruleValueMap;
            }
            // 分割字符串以获取键和值
            String[] parts = ruleValueGroup.split(Constants.COLON);
            if (parts.length != 2) {
                throw new IllegalArgumentException("rule_weight rule_rule invalid input format" + ruleValueGroup);
            }
            ruleValueMap.put(Long.parseLong(parts[0]),ruleValueGroup);
        }
        
        return ruleValueMap;
    }
}
