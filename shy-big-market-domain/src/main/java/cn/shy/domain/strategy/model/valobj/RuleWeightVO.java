package cn.shy.domain.strategy.model.valobj;

import lombok.*;
import org.checkerframework.checker.units.qual.A;

import java.util.List;

/**
 * @author shy
 * @since 2024/5/12 14:33
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RuleWeightVO {
    /**
     * 原始规则值配置
     */
    private String ruleValue;
    
    /**
     * 权重值
     */
    private Integer weight;
    
    /**
     * 奖品列表
     */
    private List<Award> awardList;
    
    /**
     * 奖品ids
     */
    private List<Integer> awardIds;
    
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Award{
        private Integer awardId;
        private String awardTitle;
    }
}
