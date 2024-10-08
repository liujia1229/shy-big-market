package cn.shy.test.domain.award;

import cn.bugstack.middleware.db.router.strategy.IDBRouterStrategy;
import cn.shy.domain.award.model.entity.UserAwardRecordEntity;
import cn.shy.domain.award.model.valobj.AwardStateVO;
import cn.shy.domain.award.service.IAwardService;
import cn.shy.infrastructure.persistent.dao.IAwardDao;
import cn.shy.infrastructure.persistent.po.Award;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author shy
 * @since 2024/4/8 21:13
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class AwardServiceTest {

    
    @Resource
    private IAwardService awardService;
    
    
    @Resource
    private IAwardDao awardDao;
    
    @Resource
    private IDBRouterStrategy dbRouter;
    /**
     * 模拟发放抽奖记录，流程中会发送MQ，以及接收MQ消息，还有 task 表，补偿发送MQ
     */
    @Test
    public void test_saveUserAwardRecord(){
        for (int i = 0; i < 20; i++) {
            UserAwardRecordEntity userAwardRecordEntity = new UserAwardRecordEntity();
            userAwardRecordEntity.setUserId("xiaofuge");
            userAwardRecordEntity.setActivityId(100301L);
            userAwardRecordEntity.setStrategyId(100006L);
            userAwardRecordEntity.setOrderId(RandomStringUtils.randomNumeric(12));
            userAwardRecordEntity.setAwardId(101);
            userAwardRecordEntity.setAwardTitle("OpenAI 增加使用次数");
            userAwardRecordEntity.setAwardTime(new Date());
            userAwardRecordEntity.setAwardState(AwardStateVO.create);
            awardService.saveUserAwardRecord(userAwardRecordEntity);
        }
    }
    @Test
    public void test_award(){
        dbRouter.doRouter("awardId");
        String s = awardDao.queryAwardKey(101);
        System.err.println(s);
        
    }
}
