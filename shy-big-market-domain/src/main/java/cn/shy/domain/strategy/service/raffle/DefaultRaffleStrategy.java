package cn.shy.domain.strategy.service.raffle;

import cn.shy.domain.strategy.model.entity.RaffleFactorEntity;
import cn.shy.domain.strategy.model.entity.RuleActionEntity;
import cn.shy.domain.strategy.model.entity.RuleMatterEntity;
import cn.shy.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import cn.shy.domain.strategy.service.rule.ILogicFilter;
import cn.shy.domain.strategy.service.rule.factory.DefaultLogicFactory;
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
 * @author shy
 * @since 2024/3/25 21:58
 */
@Slf4j
@Service
public class DefaultRaffleStrategy extends AbstractRaffleStrategy{
    
    @Resource
    private DefaultLogicFactory logicFactory;
    
    @Override
    protected RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> doCheckRaffleBeforeLogic(RaffleFactorEntity raffleFactorEntity, String... logics) {
        Map<String, ILogicFilter<RuleActionEntity.RaffleBeforeEntity>> logicFilterGroup = logicFactory.openLogicFilter();
        
        //优先黑名单
        String ruleBackList = Arrays.stream(logics)
                .filter(str -> DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode().equals(str))
                .findFirst()
                .orElse(null);
        
        if (StringUtils.isNotEmpty(ruleBackList)) {
            ILogicFilter<RuleActionEntity.RaffleBeforeEntity> ruleBackFilter = logicFilterGroup.get(DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode());
            RuleMatterEntity ruleMatterEntity = new RuleMatterEntity();
            ruleMatterEntity.setStrategyId(raffleFactorEntity.getStrategyId());
            ruleMatterEntity.setUserId(raffleFactorEntity.getUserId());
            ruleMatterEntity.setRuleModel(DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode());
            RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> ruleActionEntity = ruleBackFilter.filter(ruleMatterEntity);
            if (RuleLogicCheckTypeVO.TAKE_OVER.getCode().equals(ruleActionEntity.getCode())){
                return ruleActionEntity;
            }
        }
        
        //顺序过滤剩余规则
        List<String> ruleList = Arrays.stream(logics)
                .filter(s -> !s.equals(DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode()))
                .collect(Collectors.toList());
        
        RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> ruleActionEntity = null;
        
        for (String rule : ruleList) {
            ILogicFilter<RuleActionEntity.RaffleBeforeEntity> ruleBackFilter = logicFilterGroup.get(rule);
            RuleMatterEntity ruleMatterEntity = new RuleMatterEntity();
            ruleMatterEntity.setStrategyId(raffleFactorEntity.getStrategyId());
            ruleMatterEntity.setUserId(raffleFactorEntity.getUserId());
            ruleMatterEntity.setRuleModel(rule);
            ruleActionEntity = ruleBackFilter.filter(ruleMatterEntity);
            if (RuleLogicCheckTypeVO.TAKE_OVER.getCode().equals(ruleActionEntity.getCode())){
                // 非放行结果则顺序过滤
                log.info("抽奖前规则过滤 userId: {} ruleModel: {} code: {} info: {}", raffleFactorEntity.getUserId(), rule, ruleActionEntity.getCode(), ruleActionEntity.getInfo());
                return ruleActionEntity;
            }
        }
        
        return ruleActionEntity;
    }
}
