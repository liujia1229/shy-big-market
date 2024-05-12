package cn.shy.trigger.api.dto;

import lombok.Data;

/**
 * @author shy
 * @since 2024/5/12 14:16
 */
@Data
public class RaffleStrategyRuleWeightRequestDTO {
    
    /**
     * 用戶id
     */
    private String userId;
    
    /**
     * 活動id
     */
    private Long activityId;
}
