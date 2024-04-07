package cn.shy.domain.activity.model.entity;

import lombok.Data;

/**
 * 用户参与活动实体类
 * @author shy
 * @since 2024/4/6 14:49
 */
@Data
public class PartakeRaffleActivityEntity {
    private String userId;
    
    private Long activityId;
}
