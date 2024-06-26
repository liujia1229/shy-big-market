package cn.shy.test.domain.raffle;

import cn.shy.domain.activity.model.entity.PartakeRaffleActivityEntity;
import cn.shy.domain.activity.model.entity.UserRaffleOrderEntity;
import cn.shy.domain.activity.service.IRaffleActivityPartakeService;
import cn.shy.trigger.api.IRaffleStrategyService;
import cn.shy.trigger.api.dto.RaffleAwardListRequestDTO;
import cn.shy.trigger.api.dto.RaffleAwardListResponseDTO;
import cn.shy.types.model.Response;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author shy
 * @since 2024/4/7 9:00
 */
@SpringBootTest
@Slf4j
@RunWith(SpringRunner.class)
public class RaffleActivityPartakeServiceTest {
    
    @Resource
    private IRaffleActivityPartakeService raffleActivityPartakeService;
    
    @Resource
    private IRaffleStrategyService raffleStrategyService;
    
    @Test
    public void test_createOrder() {
        PartakeRaffleActivityEntity partakeRaffleActivityEntity = new PartakeRaffleActivityEntity();
        partakeRaffleActivityEntity.setActivityId(100301L);
        partakeRaffleActivityEntity.setUserId("xiaofuge");
        UserRaffleOrderEntity userRaffleOrderEntity = raffleActivityPartakeService.createOrder(partakeRaffleActivityEntity);
        
        log.info("入参:{}", partakeRaffleActivityEntity);
        log.info("出参:{}", userRaffleOrderEntity);
    }
    
    @Test
    public void test_queryRaffleAwardList() {
        RaffleAwardListRequestDTO request = new RaffleAwardListRequestDTO();
        request.setUserId("xiaofuge");
        request.setActivityId(100301L);
        Response<List<RaffleAwardListResponseDTO>> response = raffleStrategyService.queryRaffleAwardList(request);
        
        log.info("请求参数：{}", JSON.toJSONString(request));
        log.info("测试结果：{}", JSON.toJSONString(response));
    }
}
