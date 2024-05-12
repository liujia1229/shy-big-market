package cn.shy.trigger.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 抽奖策略规则，权重配置，查询N次可解锁奖品范围，应答对象
 * @author shy
 * @since 2024/5/12 14:13
 */
@Data
public class RaffleStrategyRuleWeightResponseDTO {
    /**
     * 权重规则配置的抽奖次数
     */
    private Integer ruleWeightCount;
    /**
     * 用户在一个活动下完成的总抽奖次数
     */
    private Integer userActivityAccountTotalUseCount;
    /**
     * 用户在一个活动下完成的总抽奖次数
     */
    private List<StrategyAward> strategyAwards;
    
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StrategyAward {
        // 奖品ID
        private Integer awardId;
        // 奖品标题
        private String awardTitle;
    }
}
