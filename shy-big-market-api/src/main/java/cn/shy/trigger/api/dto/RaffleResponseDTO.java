package cn.shy.trigger.api.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @author shy
 * @since 2024/3/30 16:39
 */
@Data
@Builder
public class RaffleResponseDTO {
    /**
     * 奖品ID
     */
    private Integer awardId;
    
    
    /**
     * 排序编号【策略奖品配置的奖品顺序编号】
     */
    private Integer awardIndex;
}
