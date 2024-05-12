package cn.shy.test.domain.rabate;


import cn.shy.domain.rebate.model.entity.BehaviorEntity;
import cn.shy.domain.rebate.model.valobj.BehaviorTypeVO;
import cn.shy.domain.rebate.service.IBehaviorRebateService;
import cn.shy.trigger.api.IRaffleActivityService;
import cn.shy.trigger.api.dto.UserActivityAccountRequestDTO;
import cn.shy.trigger.api.dto.UserActivityAccountResponseDTO;
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
 * @description 行为返利单测
 * @create 2024-04-30 17:53
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class BehaviorRebateServiceTest {

    @Resource
    private IBehaviorRebateService behaviorRebateService;
    
    @Resource
    private IRaffleActivityService raffleActivityService;

    @Test
    public void test_createOrder() {
        BehaviorEntity behaviorEntity = new BehaviorEntity();
        behaviorEntity.setUserId("shy");
        behaviorEntity.setBehaviorTypeVO(BehaviorTypeVO.SIGN);
        // 重复的 OutBusinessNo 会报错唯一索引冲突，这也是保证幂等的手段，确保不会多记账
        behaviorEntity.setOutBusinessNo("20240429");

        List<String> orderIds = behaviorRebateService.createOrder(behaviorEntity);
        log.info("请求参数：{}", JSON.toJSONString(behaviorEntity));
        log.info("测试结果：{}", JSON.toJSONString(orderIds));
    }
    
    @Test
    public void test_calendarSignRebate(){
        Response<Boolean> response = raffleActivityService.calendarSignRebate("xiaofuge");
        log.info("测试结果：{}", JSON.toJSONString(response));
    }

    
    @Test
    public void test_isCalendarSignRebate(){
        Response<Boolean> response = raffleActivityService.isCalendarSignRebate("xiaofuge");
        log.info("测试结果：{}", JSON.toJSONString(response));
    }
    
    @Test
    public void test_queryUserActivityAccount() {
        UserActivityAccountRequestDTO request = new UserActivityAccountRequestDTO();
        request.setActivityId(100301L);
        request.setUserId("xiaofuge");
        // 查询数据
        Response<UserActivityAccountResponseDTO> response = raffleActivityService.queryUserActivityAccount(request);
        log.info("请求参数：{}", JSON.toJSONString(request));
        log.info("测试结果：{}", JSON.toJSONString(response));
    }
    
    
}
