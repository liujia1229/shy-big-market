package cn.shy.infrastructure.persistent.dao;

import cn.shy.infrastructure.persistent.po.UserCreditAccount;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户积分账号
 * @author shy
 * @since 2024/7/28 2:42
 */
@Mapper
public interface IUserCreditAccountDao {
    /**
     * 查询用户积分账号
     * @param userCreditAccountReq
     * @return
     */
    UserCreditAccount queryUserCreditAccount(UserCreditAccount userCreditAccountReq);
    
    /**
     * 插入用户账户
     * @param userCreditAccount
     */
    void insert(UserCreditAccount userCreditAccount);
    
    /**
     * 更新用户余额
     * @param userCreditAccount
     */
    void updateAddMount(UserCreditAccount userCreditAccount);
    
}
