package cn.shy.domain.award.repository;

import cn.shy.domain.award.model.aggregate.UserAwardRecordAggregate;

/**
 * 奖品仓储服务
 * @author shy
 * @since 2024/4/7 20:48
 */
public interface IAwardRepository {
    void saveUserAwardRecord(UserAwardRecordAggregate userAwardRecordAggregate);
}
