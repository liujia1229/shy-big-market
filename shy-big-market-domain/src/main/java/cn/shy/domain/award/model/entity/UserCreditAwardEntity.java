package cn.shy.domain.award.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 用户积分奖品实体
 * @author shy
 * @since 2024/10/4 20:26
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserCreditAwardEntity {
    private String userId;
    
    /**
     * 积分数量
     */
    private BigDecimal creditAmount;
}
