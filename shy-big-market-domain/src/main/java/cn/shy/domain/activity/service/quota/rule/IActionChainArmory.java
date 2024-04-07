package cn.shy.domain.activity.service.quota.rule;

/**
 * @author shy
 * @since 2024/4/1 21:16
 */
public interface IActionChainArmory {
    
    IActionChain next();
    
    IActionChain appendNext(IActionChain next);
}
