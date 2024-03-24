package cn.shy.domain.strategy.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 策略结果实体
 * @author shy
 * @since 2024/3/23 22:16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AwardEntity {
    
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 奖品ID
     */
    private Integer awardId;
    
}
