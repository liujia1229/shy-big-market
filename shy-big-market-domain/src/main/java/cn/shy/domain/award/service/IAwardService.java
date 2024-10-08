package cn.shy.domain.award.service;

import cn.shy.domain.award.model.entity.DistributeAwardEntity;
import cn.shy.domain.award.model.entity.UserAwardRecordEntity;

/**
 * 奖品服务接口
 * @author shy
 * @since 2024/4/7 20:49
 */
public interface IAwardService {
    
    /**
     * 用户中奖记录保存，发奖流程
     * @param userAwardRecordEntity
     */
    void saveUserAwardRecord(UserAwardRecordEntity userAwardRecordEntity);
    
    /**
     * 用户发奖
     * @param distributeAwardEntity
     */
    void distributeAward(DistributeAwardEntity distributeAwardEntity);
}
