package cn.shy.test.domain.award;

import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import cn.shy.infrastructure.persistent.dao.IUserCreditAccountDao;
import cn.shy.infrastructure.persistent.po.UserCreditAccount;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * 用户积分账户
 * @author shy
 * @since 2024/10/5 20:17
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserCreditAccountDaoTest {
    
    @Resource
    private IDBRouterStrategy dbRouter;
    
    @Resource
    private IUserCreditAccountDao userCreditAccountDao;
    
    @Test
    public void testQueryAccount(){
        UserCreditAccount userCreditAccountReq = new UserCreditAccount();
        userCreditAccountReq.setUserId("xiaofuge23");
        
        dbRouter.doRouter(userCreditAccountReq.getUserId());
        UserCreditAccount userCreditAccount = userCreditAccountDao.queryUserCreditAccount(userCreditAccountReq);
        System.err.println(userCreditAccount);
        dbRouter.clear();
        
    }
    
}
