package cn.shy.infrastructure.persistent.dao;

import cn.bugstack.middleware.db.router.annotation.DBRouter;
import cn.shy.infrastructure.persistent.po.RaffleActivityAccount;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户抽奖账户表
 * @author shy
 * @since 2024/3/31 20:49
 */
@Mapper
public interface IRaffleActivityAccountDao {
    int updateAccountQuota(RaffleActivityAccount raffleActivityAccount);
    
    void insert(RaffleActivityAccount raffleActivityAccount);
    
    @DBRouter
    RaffleActivityAccount queryActivityAccountByUserId(RaffleActivityAccount raffleActivityAccountReq);
    
    int updateActivityAccountSubtractionQuota(RaffleActivityAccount build);
}
