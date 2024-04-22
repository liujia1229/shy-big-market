package cn.shy.trigger.api.dto;

import lombok.Data;

/**
 * 活动抽奖请求对象
 * @author shy
 * @since 2024/4/20 22:07
 */
@Data
public class ActivityDrawRequestDTO {
    /**
     * 用户id
     */
    private String userId;
    /**
     * 活动id
     */
    private Long activityId;
}
