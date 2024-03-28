package cn.shy.domain.strategy.service.rule.chain;

/**
 * @author shy
 * @since 2024/3/27 22:11
 */
public abstract class AbstractLogicChain implements ILogicChain{
    
    private ILogicChain next;
    
    @Override
    public ILogicChain next() {
        return this.next;
    }
    
    @Override
    public ILogicChain appendNext(ILogicChain next) {
        this.next = next;
        return next;
    }
    
    protected abstract String ruleModel();
    
}
