package cn.shy.trigger.api.dto;

import lombok.Data;

/**
 * 用户活动账户请求
 * @author shy
 * @since 2024/5/9 22:07
 */
@Data
public class UserActivityAccountRequestDTO {
    /**
     * 用户id
     */
    private String userId;
    /**
     * 活动id
     */
    private Long activityId;
}
