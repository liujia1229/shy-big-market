package cn.shy.domain.award.model.aggregate;

import cn.shy.domain.award.model.entity.TaskEntity;
import cn.shy.domain.award.model.entity.UserAwardRecordEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户中奖记录聚合类
 * @author shy
 * @since 2024/4/7 21:04
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAwardRecordAggregate {
    
    private UserAwardRecordEntity userAwardRecordEntity;
    
    private TaskEntity taskEntity;
}
