package cn.shy.domain.award.model.aggregate;

import cn.shy.domain.award.model.entity.UserAwardRecordEntity;
import cn.shy.domain.award.model.entity.UserCreditAwardEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户发奖聚合
 * @author shy
 * @since 2024/10/4 20:24
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GiveOutPrizesAggregate {
    
    /**
     * 用户id
     */
    private String userId;
    
    /**
     * 奖品实体
     */
    private UserAwardRecordEntity userAwardRecordEntity;
    
    /**
     * 积分实体
     */
    private UserCreditAwardEntity userCreditAwardEntity;

}
