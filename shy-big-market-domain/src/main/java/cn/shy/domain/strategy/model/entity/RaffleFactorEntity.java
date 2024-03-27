package cn.shy.domain.strategy.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 抽奖因子实体
 *
 * @author shy
 * @since 2024/3/25 21:01
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RaffleFactorEntity {
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 策略ID
     */
    private Long strategyId;
    
    /**
     * 奖品ID
     */
    private Integer awardId;
}
