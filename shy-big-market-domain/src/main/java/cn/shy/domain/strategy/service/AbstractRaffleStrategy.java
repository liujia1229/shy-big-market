package cn.shy.domain.strategy.service;

import cn.shy.domain.strategy.model.entity.*;
import cn.shy.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import cn.shy.domain.strategy.model.valobj.StrategyAwardRuleModelVO;
import cn.shy.domain.strategy.repository.IStrategyRepository;
import cn.shy.domain.strategy.service.IRaffleStrategy;
import cn.shy.domain.strategy.service.armory.IStrategyDispatch;
import cn.shy.domain.strategy.service.rule.chain.ILogicChain;
import cn.shy.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import cn.shy.domain.strategy.service.rule.filter.factory.DefaultLogicFactory;
import cn.shy.types.enums.ResponseCode;
import cn.shy.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;

/**
 * @author shy
 * @since 2024/3/25 21:43
 */
@Slf4j
public abstract class AbstractRaffleStrategy implements IRaffleStrategy {
    
    @Resource
    private IStrategyRepository strategyRepository;
    
    
    @Resource
    private DefaultChainFactory defaultChainFactory;
    
    @Override
    public RaffleAwardEntity performRaffle(RaffleFactorEntity raffleFactorEntity) {
        // 1. 参数校验
        String userId = raffleFactorEntity.getUserId();
        Long strategyId = raffleFactorEntity.getStrategyId();
        if (null == strategyId || StringUtils.isBlank(userId)) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
        }
        // 2. 获取抽奖责任链 - 前置规则的责任链处理
        ILogicChain logicChain = defaultChainFactory.openLogicChain(strategyId);
        
        // 3. 通过责任链获得，奖品ID
        Integer awardId = logicChain.logic(userId, strategyId);
        
        // 4. 查询奖品规则「抽奖中（拿到奖品ID时，过滤规则）、抽奖后（扣减完奖品库存后过滤，抽奖中拦截和无库存则走兜底）」
        StrategyAwardRuleModelVO strategyAwardRuleModelVO = strategyRepository.queryStrategyAwardRuleModelVO(strategyId, awardId);
        
        // 6. 抽奖中 - 规则过滤
        RuleActionEntity<RuleActionEntity.RaffleCenterEntity> ruleActionCenterEntity = this.doCheckRafflCenterLogic(RaffleFactorEntity.builder()
                .strategyId(strategyId)
                .awardId(awardId)
                .userId(userId)
                .build(), strategyAwardRuleModelVO.raffleCenterRuleModelList());
        
        if (RuleLogicCheckTypeVO.TAKE_OVER.getCode().equals(ruleActionCenterEntity.getCode())) {
            log.info("【临时日志】中奖中规则拦截，通过抽奖后规则 rule_luck_award 走兜底奖励。");
            return RaffleAwardEntity.builder()
                    .awardDesc("中奖中规则拦截，通过抽奖后规则 rule_luck_award 走兜底奖励。")
                    .build();
        }
        
        return RaffleAwardEntity.builder()
                .awardId(awardId)
                .build();
    }
    
    protected abstract RuleActionEntity<RuleActionEntity.RaffleCenterEntity> doCheckRafflCenterLogic(RaffleFactorEntity build, String... logics);
}
