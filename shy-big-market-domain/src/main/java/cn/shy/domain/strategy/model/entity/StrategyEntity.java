package cn.shy.domain.strategy.model.entity;

import cn.shy.types.common.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 策略实体类
 *
 * @author shy
 * @since 2024/3/24 17:55
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StrategyEntity {
    /**
     * 抽奖策略ID
     */
    private Long strategyId;
    /**
     * 抽奖策略描述
     */
    private String strategyDesc;
    /**
     * 抽奖规则模型 rule_weight,rule_blacklist
     */
    private String ruleModels;
    
    public String getRuleWeight() {
        String[] ruleModels = this.roleModels();
        for (String ruleModel : ruleModels) {
            if ("rule_weight".equals(ruleModel)){
                return ruleModel;
            }
        }
        return null;
    }
    
    private String[] roleModels() {
        if (ruleModels == null || "".equals(ruleModels)){
            return null;
        }
        return ruleModels.split(Constants.SPLIT);
    }
}
