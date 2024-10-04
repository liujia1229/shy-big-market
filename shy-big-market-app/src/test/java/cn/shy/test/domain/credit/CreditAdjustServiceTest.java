package cn.shy.test.domain.credit;

import cn.shy.domain.credit.model.entity.TradeEntity;
import cn.shy.domain.credit.model.valobj.TradeNameVO;
import cn.shy.domain.credit.model.valobj.TradeTypeVO;
import cn.shy.domain.credit.service.ICreditAdjustService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * @author shy
 * @since 2024/7/28 15:36
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class CreditAdjustServiceTest {

    @Resource
    private ICreditAdjustService creditService;

    @Test
    public void testCreditCreateOder(){
        TradeEntity tradeEntity = new TradeEntity();
        tradeEntity.setUserId("shy");
        tradeEntity.setTradeName(TradeNameVO.REBATE);
        tradeEntity.setTradeType(TradeTypeVO.FORWARD);
        tradeEntity.setAmount(new BigDecimal("18.48"));
        tradeEntity.setOutBusinessNo("100220011");
        creditService.createOder(tradeEntity);
    }

}
