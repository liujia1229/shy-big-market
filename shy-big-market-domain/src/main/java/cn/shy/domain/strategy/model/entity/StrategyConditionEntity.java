package cn.shy.domain.strategy.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 策略条件实体
 * @author shy
 * @since 2024/3/23 22:16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StrategyConditionEntity {
    
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 策略ID
     */
    private Integer strategyId;
    
}