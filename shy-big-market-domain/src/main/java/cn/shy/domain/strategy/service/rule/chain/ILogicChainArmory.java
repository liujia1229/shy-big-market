package cn.shy.domain.strategy.service.rule.chain;

/**
 * 责任链装配
 * @author shy
 * @since 2024/3/27 22:08
 */
public interface ILogicChainArmory {
    
    ILogicChain next();
    
    ILogicChain appendNext(ILogicChain next);
}
