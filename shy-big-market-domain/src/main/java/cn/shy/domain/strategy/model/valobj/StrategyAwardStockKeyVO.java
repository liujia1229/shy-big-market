package cn.shy.domain.strategy.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 策略奖品库存Key标识值对象
 * @author shy
 * @since 2024/3/29 15:36
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StrategyAwardStockKeyVO {
    
    // 策略ID
    private Long strategyId;
    // 奖品ID
    private Integer awardId;
    
}