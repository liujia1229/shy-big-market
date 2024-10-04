package cn.shy.domain.credit.model.entity;

import cn.shy.domain.credit.model.valobj.TradeNameVO;
import cn.shy.domain.credit.model.valobj.TradeTypeVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 增加实体对象
 *
 * @author shy
 * @since 2024/7/28 3:29
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TradeEntity {
    /**
     * 用户id
     */
    private String userId;
    
    /**
     * 交易名称
     */
    private TradeNameVO tradeName;
    /**
     * 交易类型
     */
    private TradeTypeVO tradeType;
    
    /**
     * 交易金额
     */
    private BigDecimal amount;
    /**
     * 业务防重id,外部透传
     */
    private String outBusinessNo;
    
}
