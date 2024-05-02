package cn.shy.domain.rebate.model.entity;

import cn.shy.domain.rebate.model.valobj.BehaviorTypeVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 行为对象实体
 * @author shy
 * @since 2024/5/1 21:30
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BehaviorEntity {
    
    /**
     * 用户id
     */
    private String userId;
    
    /**
     * 用户行为类型 sign / openai 支付
     */
    private BehaviorTypeVO behaviorTypeVO;
    
    /**
     * 业务id,签到为日期字符串 支付为外部业务id
     */
    private String outBusinessNo;
}
