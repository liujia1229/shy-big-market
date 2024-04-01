package cn.shy.infrastructure.persistent.dao;

import cn.bugstack.middleware.db.router.annotation.DBRouter;
import cn.bugstack.middleware.db.router.annotation.DBRouterStrategy;
import cn.shy.infrastructure.persistent.po.RaffleActivityOrder;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author shy
 * @since 2024/3/31 20:52
 */
@Mapper
@DBRouterStrategy(splitTable = true)
public interface IRaffleActivityOrderDao {
    
    @DBRouter(key = "userId")
    void insert(RaffleActivityOrder raffleActivityOrder);
    
    @DBRouter
    List<RaffleActivityOrder> queryRaffleActivityOrderByUserId(String userId);
}
