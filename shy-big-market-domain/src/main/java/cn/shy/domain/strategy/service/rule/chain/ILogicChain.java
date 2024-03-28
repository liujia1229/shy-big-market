package cn.shy.domain.strategy.service.rule.chain;

/**
 * 抽奖策略规则责任链接口
 * @author shy
 * @since 2024/3/27 22:07
 */
public interface ILogicChain extends ILogicChainArmory{
    
    /**
     * 调用责任链过滤
     * @param userId
     * @param strategyId
     * @return
     */
    Integer logic(String userId,Long strategyId);
}
