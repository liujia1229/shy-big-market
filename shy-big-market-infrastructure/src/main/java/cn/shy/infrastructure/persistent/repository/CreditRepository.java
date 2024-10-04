package cn.shy.infrastructure.persistent.repository;

import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import cn.shy.domain.credit.model.aggregate.TradeAggregate;
import cn.shy.domain.credit.model.entity.CreditAccountEntity;
import cn.shy.domain.credit.model.entity.CreditOrderEntity;
import cn.shy.domain.credit.model.valobj.AccountStatusVO;
import cn.shy.domain.credit.repository.ICreditRepository;
import cn.shy.infrastructure.persistent.dao.IUserCreditAccountDao;
import cn.shy.infrastructure.persistent.dao.IUserCreditOrderDao;
import cn.shy.infrastructure.persistent.po.UserCreditAccount;
import cn.shy.infrastructure.persistent.po.UserCreditOrder;
import cn.shy.infrastructure.persistent.redis.IRedisService;
import cn.shy.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;
import org.yaml.snakeyaml.constructor.DuplicateKeyException;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

/**
 * 用户积分模块仓储实现
 *
 * @author shy
 * @since 2024/7/28 2:36
 */
@Repository
@Slf4j
public class CreditRepository implements ICreditRepository {
    
    @Resource
    private IUserCreditAccountDao userCreditAccountDao;
    @Resource
    private IUserCreditOrderDao userCreditOrderDao;
    @Resource
    private IDBRouterStrategy dbRouter;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private IRedisService redisService;
    
    @Override
    public void saveUserCreditTradeOrder(TradeAggregate tradeAggregate) {
        CreditAccountEntity creditAccountEntity = tradeAggregate.getCreditAccountEntity();
        CreditOrderEntity creditOrderEntity = tradeAggregate.getCreditOrderEntity();
        String userId = tradeAggregate.getUserId();
        
        //积分账户
        UserCreditAccount userCreditAccountReq = new UserCreditAccount();
        userCreditAccountReq.setUserId(userId);
        userCreditAccountReq.setTotalAmount(creditAccountEntity.getAdjustAmount());
        // 知识；仓储往上有业务语义，仓储往下到 dao 操作是没有业务语义的。所以不用在乎这块使用的字段名称，直接用持久化对象即可。
        userCreditAccountReq.setAvailableAmount(creditAccountEntity.getAdjustAmount());
        userCreditAccountReq.setAccountStatus(AccountStatusVO.open.getCode());
        
        
        // 积分订单
        UserCreditOrder userCreditOrderReq = new UserCreditOrder();
        userCreditOrderReq.setUserId(creditOrderEntity.getUserId());
        userCreditOrderReq.setOrderId(creditOrderEntity.getOrderId());
        userCreditOrderReq.setTradeName(creditOrderEntity.getTradeName().getName());
        userCreditOrderReq.setTradeType(creditOrderEntity.getTradeType().getCode());
        userCreditOrderReq.setTradeAmount(creditOrderEntity.getTradeAmount());
        userCreditOrderReq.setOutBusinessNo(creditOrderEntity.getOutBusinessNo());
        
        RLock lock = redisService.getLock(Constants.RedisKey.USER_CREDIT_ACCOUNT_LOCK + userId + Constants.UNDERLINE + userCreditOrderReq.getOutBusinessNo());
        
        try {
            lock.lock(3, TimeUnit.SECONDS);
            dbRouter.doRouter(userId);
            transactionTemplate.execute(status -> {
                try {
                    //查账户
                    UserCreditAccount userCreditAccount
                            = userCreditAccountDao.queryUserCreditAccount(userCreditAccountReq);
                    
                    if (userCreditAccount == null){
                        //不存在 新增账户
                        userCreditAccountDao.insert(userCreditAccountReq);
                    }else {
                        //存在 修改金额
                        userCreditAccountDao.updateAddMount(userCreditAccountReq);
                    }
                    //新增订单
                    userCreditOrderDao.insert(userCreditOrderReq);
                } catch (DuplicateKeyException e) {
                    status.setRollbackOnly();
                    log.error("调整账户积分额度异常，唯一索引冲突 userId:{} orderId:{}", userId, creditOrderEntity.getOrderId(), e);
                } catch (Exception e) {
                    status.setRollbackOnly();
                    log.error("调整账户积分额度失败 userId:{} orderId:{}", userId, creditOrderEntity.getOrderId(), e);
                }
                return 1;
            });
            
        } finally {
            dbRouter.clear();
            lock.unlock();
        }
    }
    
    @Override
    public CreditAccountEntity queryUserCreditAccount(String userId) {
        UserCreditAccount userCreditAccountReq = new UserCreditAccount();
        userCreditAccountReq.setUserId(userId);
        try {
            dbRouter.doRouter(userId);
            UserCreditAccount userCreditAccount = userCreditAccountDao.queryUserCreditAccount(userCreditAccountReq);
            BigDecimal availableAmount = BigDecimal.ZERO;
            if (userCreditAccount != null) {
                availableAmount = userCreditAccount.getAvailableAmount();
            }
            CreditAccountEntity.builder()
                    .userId(userId)
                    .adjustAmount(availableAmount)
                    .build();
        } finally {
            dbRouter.clear();
        }
        return null;
    }
}
