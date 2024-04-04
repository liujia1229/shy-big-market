package cn.shy.test.raffle;

import cn.shy.domain.activity.model.entity.SkuRechargeEntity;
import cn.shy.domain.activity.service.IRaffleOrder;
import cn.shy.domain.activity.service.rule.armory.IActivityArmory;
import cn.shy.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;

/**
 * @author shy
 * @since 2024/4/1 22:05
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class RaffleOrderTest {
    
    @Resource
    private IRaffleOrder raffleOrder;
    
    @Resource
    private IActivityArmory activityArmory;
    
    @Before
    public void setUp(){
        activityArmory.assembleActivitySku(9011l);
    }
    
    
    /**
     * 测试库存消耗和最终一致更新
     * 1. raffle_activity_sku 库表库存可以设置20个
     * 2. 清空 redis 缓存 flushall
     * 3. for 循环20次，消耗完库存，最终数据库剩余库存为0
     */
    @Test
    public void test_createSkuRechargeOrder() throws InterruptedException {
        for (int i = 0; i < 20; i++) {
            try {
                SkuRechargeEntity skuRechargeEntity = new SkuRechargeEntity();
                skuRechargeEntity.setUserId("shy");
                skuRechargeEntity.setSku(9011l);
                skuRechargeEntity.setOutBusinessNo(RandomStringUtils.randomNumeric(12));
                String orderId = raffleOrder.createSkuRechargeOrder(skuRechargeEntity);
                log.info("测试结果：{}", orderId);
            }catch (AppException e){
                log.warn(e.getInfo());
            }
        }
        new CountDownLatch(1).await();
    }
    
}
