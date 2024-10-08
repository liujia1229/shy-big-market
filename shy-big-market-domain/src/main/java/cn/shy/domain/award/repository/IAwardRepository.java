package cn.shy.domain.award.repository;

import cn.shy.domain.award.model.aggregate.GiveOutPrizesAggregate;
import cn.shy.domain.award.model.aggregate.UserAwardRecordAggregate;

/**
 * 奖品仓储服务
 * @author shy
 * @since 2024/4/7 20:48
 */
public interface IAwardRepository {
    void saveUserAwardRecord(UserAwardRecordAggregate userAwardRecordAggregate);
    
    /**
     * 查询用户奖品配置
     * @param awardId
     * @return
     */
    String queryAwardConfig(Integer awardId);
    
    /**
     * 存储发奖对象
     * @param giveOutPrizesAggregate
     */
    void saveGiveOutPrizesAggregate(GiveOutPrizesAggregate giveOutPrizesAggregate);
    
    /**
     * 查询奖品key
     * @param awardId
     * @return
     */
    String queryAwardKey(Integer awardId);
    
}
