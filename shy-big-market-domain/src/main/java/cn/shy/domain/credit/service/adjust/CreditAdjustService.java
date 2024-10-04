package cn.shy.domain.credit.service.adjust;

import cn.shy.domain.credit.model.aggregate.TradeAggregate;
import cn.shy.domain.credit.model.entity.CreditAccountEntity;
import cn.shy.domain.credit.model.entity.CreditOrderEntity;
import cn.shy.domain.credit.model.entity.TradeEntity;
import cn.shy.domain.credit.repository.ICreditRepository;
import cn.shy.domain.credit.service.ICreditAdjustService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author shy
 * @since 2024/7/28 2:32
 */
@Service
@Slf4j
public class CreditAdjustService implements ICreditAdjustService {
    
    private final ICreditRepository creditRepository;
    
    public CreditAdjustService(ICreditRepository creditRepository){
        this.creditRepository = creditRepository;
    }
    
    @Override
    public String createOder(TradeEntity tradeEntity) {
        log.info("账户积分额度开始 userId:{} tradeName:{} amount:{}", tradeEntity.getUserId(), tradeEntity.getTradeName(), tradeEntity.getAmount());
        
        //1.创建积分账户实体
        CreditAccountEntity creditAccountEntity = TradeAggregate.createCreditAccountEntity(
                tradeEntity.getUserId(),
                tradeEntity.getAmount());
        
        //2.创建积分订单实体
        CreditOrderEntity creditOrderEntity = TradeAggregate.createCreditOrderEntity(
                tradeEntity.getUserId(),
                tradeEntity.getTradeName(),
                tradeEntity.getTradeType(),
                tradeEntity.getAmount(),
                tradeEntity.getOutBusinessNo());
        
        //3.聚合对象
        TradeAggregate tradeAggregate = TradeAggregate.builder()
                .creditOrderEntity(creditOrderEntity)
                .creditAccountEntity(creditAccountEntity)
                .userId(tradeEntity.getUserId())
                .build();
        
        //4.保存
        creditRepository.saveUserCreditTradeOrder(tradeAggregate);
        log.info("账户积分额度完成 userId:{} orderId:{}", tradeEntity.getUserId(), creditOrderEntity.getOrderId());
        
        return creditOrderEntity.getOrderId();
    }
}
