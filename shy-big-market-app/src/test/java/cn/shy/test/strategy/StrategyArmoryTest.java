package cn.shy.test.strategy;

import cn.shy.domain.strategy.service.armory.IStrategyArmory;
import cn.shy.domain.strategy.service.armory.IStrategyDispatch;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author shy
 * @since 2024/3/24 17:06
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class StrategyArmoryTest {

    @Resource
    private IStrategyDispatch strategyDispatch;
    
    @Resource
    private IStrategyArmory strategyArmory;

    
    @Test
    public void testAssembleLotteryStrategy(){
        boolean flag = strategyArmory.assembleLotteryStrategy(100001l);
        log.info("result:{}",flag);
    }
    @Test
    public void testGetRandomAwardId(){
        log.info("RandomAwardId:{}",strategyDispatch.getRandomAwardId(100001l));
        log.info("RandomAwardId:{}",strategyDispatch.getRandomAwardId(100001l));
        log.info("RandomAwardId:{}",strategyDispatch.getRandomAwardId(100001l));
    }
    
    @Before
    public void test_strategyArmory() {
        boolean success = strategyArmory.assembleLotteryStrategy(100001L);
        log.info("测试结果：{}", success);
    }
    
    /**
     * 从装配的策略中随机获取奖品ID值
     */
    @Test
    public void test_getRandomAwardId() {
        log.info("测试结果：{} - 奖品ID值", strategyDispatch.getRandomAwardId(100001L));
    }
    
    /**
     * 根据策略ID+权重值，从装配的策略中随机获取奖品ID值
     */
    @Test
    public void test_getRandomAwardId_ruleWeightValue() {
        log.info("测试结果：{} - 4000 策略配置", strategyDispatch.getRandomAwardId(100001L, "4000:102,103,104,105"));
        log.info("测试结果：{} - 5000 策略配置", strategyDispatch.getRandomAwardId(100001L, "5000:102,103,104,105,106,107"));
        log.info("测试结果：{} - 6000 策略配置", strategyDispatch.getRandomAwardId(100001L, "6000:102,103,104,105,106,107,108,109"));
    }
}
