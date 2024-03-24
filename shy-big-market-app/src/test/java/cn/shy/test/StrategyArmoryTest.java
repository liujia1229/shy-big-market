package cn.shy.test;

import cn.shy.domain.strategy.service.armory.IStrategyArmory;
import lombok.extern.slf4j.Slf4j;
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
    private IStrategyArmory strategyArmory;

    
    @Test
    public void testAssembleLotteryStrategy(){
        boolean flag = strategyArmory.assembleLotteryStrategy(100001l);
        log.info("result:{}",flag);
    }
    @Test
    public void testGetRandomAwardId(){
        log.info("RandomAwardId:{}",strategyArmory.getRandomAwardId(100001l));
        log.info("RandomAwardId:{}",strategyArmory.getRandomAwardId(100001l));
        log.info("RandomAwardId:{}",strategyArmory.getRandomAwardId(100001l));
    }
}
