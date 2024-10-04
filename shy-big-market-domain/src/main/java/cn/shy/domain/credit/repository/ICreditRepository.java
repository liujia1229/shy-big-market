package cn.shy.domain.credit.repository;

import cn.shy.domain.credit.model.aggregate.TradeAggregate;
import cn.shy.domain.credit.model.entity.CreditAccountEntity;

/**
 * 用户积分模块仓储
 *
 * @author shy
 * @since 2024/7/28 2:25
 */
public interface ICreditRepository {
    
    /**
     * 保存用户积分账户信息
     * @param tradeAggregate
     */
    void saveUserCreditTradeOrder(TradeAggregate tradeAggregate);
    
    /**
     * 查询用户积分账户
     * @param userId
     * @return
     */
    CreditAccountEntity queryUserCreditAccount(String userId);
}
