package cn.shy.infrastructure.persistent.dao;

import cn.bugstack.middleware.db.router.annotation.DBRouterStrategy;
import cn.shy.infrastructure.persistent.po.UserCreditOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户积分订单
 * @author shy
 * @since 2024/7/28 2:42
 */
@Mapper
@DBRouterStrategy(splitTable = true)
public interface IUserCreditOrderDao {
    /**
     * 用户订单新增
     * @param userCreditOrderReq
     */
    void insert(UserCreditOrder userCreditOrderReq);
    
}
