package cn.shy.domain.award.service.distribute.impl;

import cn.shy.domain.award.model.aggregate.GiveOutPrizesAggregate;
import cn.shy.domain.award.model.entity.DistributeAwardEntity;
import cn.shy.domain.award.model.entity.UserAwardRecordEntity;
import cn.shy.domain.award.model.entity.UserCreditAwardEntity;
import cn.shy.domain.award.model.valobj.AwardStateVO;
import cn.shy.domain.award.repository.IAwardRepository;
import cn.shy.domain.award.service.distribute.IDistributeAward;
import cn.shy.types.common.Constants;
import cn.shy.types.utils.RandomUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * 发用户积分奖品
 *
 * @author shy
 * @since 2024/10/4 19:58
 */
@Slf4j
@Component("user_credit_random")
public class UserCreditRandomAward implements IDistributeAward {
    
    @Resource
    private IAwardRepository repository;
    
    /**
     * 发用户积分奖品
     *
     * @param distributeAwardEntity
     */
    @Override
    public void giveOutPrizes(DistributeAwardEntity distributeAwardEntity) {
        //奖品id
        Integer awardId = distributeAwardEntity.getAwardId();
        //查询奖品配置:发积分的范围
        String awardConfig = distributeAwardEntity.getAwardConfig();
        if (StringUtils.isBlank(awardConfig)) {
            awardConfig = repository.queryAwardConfig(awardId);
        }
        
        //生成随机积分:按照配置的范围
        String[] creditRange = awardConfig.split(Constants.SPLIT);
        if (creditRange.length != 2) {
            log.error("award_config={} 应该是一个范围值如:1,100", awardConfig);
            throw new RuntimeException("award_config=" + awardConfig + " 应该是一个范围值如:1,100");
        }
        BigDecimal randomCredit = RandomUtils.
                getRandomBigDecimal(new BigDecimal(creditRange[0]), new BigDecimal(creditRange[1]), 2);
        
        //构建发奖聚合对象
        UserAwardRecordEntity userAwardRecordEntity = UserAwardRecordEntity.builder()
                .userId(distributeAwardEntity.getUserId())
                .awardId(distributeAwardEntity.getAwardId())
                .orderId(distributeAwardEntity.getOrderId())
                .awardState(AwardStateVO.complete)
                .build();

        //构建积分奖品对象
        UserCreditAwardEntity userCreditAwardEntity = UserCreditAwardEntity.builder()
                .creditAmount(randomCredit)
                .userId(distributeAwardEntity.getUserId())
                .build();
        
        GiveOutPrizesAggregate giveOutPrizesAggregate = GiveOutPrizesAggregate.builder()
                .userId(distributeAwardEntity.getUserId())
                .userAwardRecordEntity(userAwardRecordEntity)
                .userCreditAwardEntity(userCreditAwardEntity)
                .build();
        
        //发奖
        repository.saveGiveOutPrizesAggregate(giveOutPrizesAggregate);
    }
}
