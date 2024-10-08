package cn.shy.domain.award.service.distribute;

import cn.shy.domain.award.model.entity.DistributeAwardEntity;

/**
 * 发奖统一实现接口
 * @author shy
 * @since 2024/10/4 19:49
 */
public interface IDistributeAward {
    
    /**
     * 发奖
     * @param distributeAwardEntity
     */
    void giveOutPrizes(DistributeAwardEntity distributeAwardEntity);
    
}
