package cn.shy.domain.rebate.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 日常返利配置值对象
 * @author shy
 * @since 2024/5/1 21:35
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DailyBehaviorRebateVO {
    
    /**
     * 行为类型（sign 签到、openai_pay 支付）
     */
    private String behaviorType;
    /**
     * 返利描述
     */
    private String rebateDesc;
    /**
     * 返利类型（sku 活动库存充值商品、integral 用户活动积分）
     */
    private String rebateType;
    /**
     * 返利配置
     */
    private String rebateConfig;
}
