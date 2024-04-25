package cn.shy.trigger.api.dto;

import lombok.Data;

/**
 * @author shy
 * @since 2024/3/30 16:34
 */
@Data
public class RaffleAwardListRequestDTO {
    
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 抽奖活动ID
     */
    private Long activityId;
}
