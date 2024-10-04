package cn.shy.domain.credit.model.entity;

import cn.shy.domain.credit.model.valobj.TradeNameVO;
import cn.shy.domain.credit.model.valobj.TradeTypeVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 用户账户实体
 * @author shy
 * @since 2024/7/28 2:34
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreditAccountEntity {
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 可用积分，每次扣减的值
     */
    private BigDecimal adjustAmount;
}
