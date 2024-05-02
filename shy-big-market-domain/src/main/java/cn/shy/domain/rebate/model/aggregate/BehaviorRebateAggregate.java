package cn.shy.domain.rebate.model.aggregate;

import cn.shy.domain.rebate.model.entity.BehaviorRebateOrderEntity;
import cn.shy.domain.rebate.model.entity.TaskEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 行为返利对象
 * @author shy
 * @since 2024/5/1 21:36
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BehaviorRebateAggregate {
    /** 用户ID */
    private String userId;
    /** 行为返利订单实体对象 */
    private BehaviorRebateOrderEntity behaviorRebateOrderEntity;
    /** 任务实体对象 */
    private TaskEntity taskEntity;
}
