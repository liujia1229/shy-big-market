package cn.shy.infrastructure.persistent.dao;

import cn.bugstack.middleware.db.router.annotation.DBRouter;
import cn.shy.infrastructure.persistent.po.RaffleActivityAccountDay;
import org.apache.ibatis.annotations.Mapper;

/**
 * 抽奖活动账户表-日次数
 * @author shy
 * @since 2024/4/4 16:39
 */
@Mapper
public interface IRaffleActivityAccountDayDao {
    @DBRouter
    RaffleActivityAccountDay queryActivityAccountDayByUserId(RaffleActivityAccountDay raffleActivityAccountDayReq);
    
    int updateActivityAccountDaySubtractionQuota(RaffleActivityAccountDay raffleActivityAccountDay);
    
    void insertActivityAccountDay(RaffleActivityAccountDay raffleActivityAccountDay);
    
    @DBRouter
    Integer queryRaffleActivityAccountDayPartakeCount(RaffleActivityAccountDay raffleActivityAccountDayReq);
    
}
