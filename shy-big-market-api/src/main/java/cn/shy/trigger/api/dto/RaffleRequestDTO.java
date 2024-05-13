package cn.shy.trigger.api.dto;

import lombok.Data;

/**
 * 抽奖请求参数
 * @author shy
 * @since 2024/3/30 16:39
 */
@Data
public class RaffleRequestDTO {
    
    /**
     * 策略id
     */
    private Long strategyId;
    
    /**
     * 用户id
     */
    private String userId;
}
