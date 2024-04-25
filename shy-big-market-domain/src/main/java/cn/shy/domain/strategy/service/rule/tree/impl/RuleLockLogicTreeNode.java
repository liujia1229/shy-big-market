package cn.shy.domain.strategy.service.rule.tree.impl;

import cn.shy.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import cn.shy.domain.strategy.repository.IStrategyRepository;
import cn.shy.domain.strategy.service.rule.tree.ILogicTreeNode;
import cn.shy.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 次数锁节点
 *
 * @author shy
 * @since 2024/3/28 15:20
 */
@Slf4j
@Component("rule_lock")
public class RuleLockLogicTreeNode implements ILogicTreeNode {
    
    @Resource
    private IStrategyRepository strategyRepository;
    
    @Override
    public DefaultTreeFactory.TreeActionEntity logic(String userId, Long strategyId, Integer awardId, String ruleValue, Date endDateTime) {
        log.info("规则过滤-次数锁 userId:{} strategyId:{} awardId:{}", userId, strategyId, awardId);
        
        long raffleCount = 0L;
        try {
            raffleCount = Long.parseLong(ruleValue);
        }catch (Exception e){
            throw new RuntimeException("规则过滤-次数锁异常 ruleValue: " + ruleValue + " 配置不正确");
        }
        // 查询用户抽奖次数 - 当天的；策略ID:活动ID 1:1 的配置，可以直接用 strategyId 查询。
        Integer userRaffleCount = strategyRepository.queryTodayUserRaffleCount(userId,strategyId);
        //用户抽奖次数超过解锁次数,直接放行
        if (userRaffleCount >= raffleCount){
            return DefaultTreeFactory.TreeActionEntity.builder()
                    .ruleLogicCheckType(RuleLogicCheckTypeVO.ALLOW)
                    .build();
        }
        // 用户抽奖次数小于规则限定值，规则拦截
        return DefaultTreeFactory.TreeActionEntity.builder()
                .ruleLogicCheckType(RuleLogicCheckTypeVO.TAKE_OVER)
                .build();
    }
}
