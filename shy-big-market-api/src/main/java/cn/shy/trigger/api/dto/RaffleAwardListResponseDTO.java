package cn.shy.trigger.api.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 抽奖奖品列表，应答对象
 * @author shy
 * @since 2024/3/30 16:34
 */
@Data
@Builder
public class RaffleAwardListResponseDTO {
    /**
     * 奖品ID
     */
    private Integer awardId;
    /**
     * 奖品标题
     */
    private String awardTitle;
    /**
     * 奖品副标题【抽奖1次后解锁】
     */
    private String awardSubtitle;
    /**
     * 排序编号
     */
    private Integer sort;
    
    /**
     * 解锁次数
     */
    private Integer awardRuleLockCount;
    
    /**
     * 奖品是否解锁
     */
    private Boolean isAwardUnlock;
    
    /**
     * 等待解锁次数
     */
    private Integer waitUnlockCount;
    
}
