package cn.shy.domain.strategy.model.valobj;

import cn.shy.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import cn.shy.types.common.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 抽奖策略规则规则值对象；值对象，没有唯一ID，仅限于从数据库查询对象
 * @author shy
 * @since 2024/3/26 22:07
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StrategyAwardRuleModelVO {
    
    private String ruleModels;
}
