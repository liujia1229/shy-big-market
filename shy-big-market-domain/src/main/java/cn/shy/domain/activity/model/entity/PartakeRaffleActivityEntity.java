package cn.shy.domain.activity.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户参与活动实体类
 * @author shy
 * @since 2024/4/6 14:49
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartakeRaffleActivityEntity {
    private String userId;
    
    private Long activityId;
}
