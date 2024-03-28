package cn.shy.domain.strategy.service.rule.chain.factory;

import cn.shy.domain.strategy.model.entity.StrategyEntity;
import cn.shy.domain.strategy.repository.IStrategyRepository;
import cn.shy.domain.strategy.service.rule.chain.ILogicChain;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 责任链工厂
 * @author shy
 * @since 2024/3/28 13:51
 */
@Service
public class DefaultChainFactory {
    
    @Resource
    private Map<String, ILogicChain> logicChainMap;
    
    @Resource
    private IStrategyRepository strategyRepository;
    

    public ILogicChain openLogicChain(Long strategyId){
        StrategyEntity strategyEntity = strategyRepository.queryStrategyEntityByStrategyId(strategyId);
        String[] ruleModels = strategyEntity.roleModels();
        if (ruleModels == null || ruleModels.length == 0){
            return logicChainMap.get("default");
        }
        ILogicChain logicChain = logicChainMap.get(ruleModels[0]);
        ILogicChain cur = logicChain;
        for (int i = 1; i < ruleModels.length; i++) {
            ILogicChain next = logicChainMap.get(ruleModels[i]);
            cur = cur.appendNext(next);
        }
        cur.appendNext(logicChainMap.get("default"));
        return logicChain;
    }
}
