package cn.shy.test.domain.strategy;

import cn.shy.infrastructure.persistent.dao.IAwardDao;
import cn.shy.infrastructure.persistent.redis.IRedisService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Optional;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApiTest {

    @Resource
    private IAwardDao awardDao;
    
    @Resource
    private IRedisService redisService;
    
    @Test
    public void test() {
        System.err.println(awardDao.queryAwardList());
        log.info("测试完成");
    }
    
    @Test
    public void testRedis(){
        redisService.setValue("k1","v1");
    }

}
