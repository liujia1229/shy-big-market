package cn.shy.domain.award.service;

import cn.shy.domain.award.model.entity.UserAwardRecordEntity;

/**
 * 奖品服务接口
 * @author shy
 * @since 2024/4/7 20:49
 */
public interface IAwardService {

    void saveUserAwardRecord(UserAwardRecordEntity userAwardRecordEntity);
}
