package cn.shy.domain.strategy.service.armory.impl;

import cn.shy.domain.strategy.model.entity.StrategyAwardEntity;
import cn.shy.domain.strategy.repository.IStrategyRepository;
import cn.shy.domain.strategy.service.armory.IStrategyArmory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.*;

/**
 * @author shy
 * @since 2024/3/23 22:18
 */
@Service
@Slf4j
public class StrategyArmory implements IStrategyArmory {
    
    @Resource
    private IStrategyRepository strategyRepository;
    
    @Override
    public boolean assembleLotteryStrategy(Long strategyId) {
        //1.查询策略配置
        List<StrategyAwardEntity> strategyAwardEntityList = strategyRepository.queryStrategyAwardList(strategyId);
        
        //2.获取最小概率
        BigDecimal minAwardRate = strategyAwardEntityList.stream()
                .map(StrategyAwardEntity::getAwardRate)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        
        //3.获取概率总和
        BigDecimal rateTotal = strategyAwardEntityList.stream()
                .map(StrategyAwardEntity::getAwardRate)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        //4.用 1 % 0.0001 获得概率范围，百分位、千分位、万分位
        BigDecimal rateRange = rateTotal.divide(minAwardRate,0, RoundingMode.CEILING);
        
        //5. 生成策略奖品概率查找表「这里指需要在list集合中，存放上对应的奖品占位即可，占位越多等于概率越高」
        List<Integer> strategyAwardSearchRateTables = new ArrayList<>(rateRange.intValue());
        for (StrategyAwardEntity strategyAwardEntity : strategyAwardEntityList) {
            Integer awardId = strategyAwardEntity.getAwardId();
            BigDecimal awardRate = strategyAwardEntity.getAwardRate();
            for (int i = 0; i < rateRange.multiply(awardRate).setScale(0, RoundingMode.CEILING).intValue(); i++) {
                strategyAwardSearchRateTables.add(awardId);
            }
        }
        
        //6.乱序
        Collections.shuffle(strategyAwardSearchRateTables);
        
        //7.映射为对应的map key值，对应的就是后续的概率值。通过概率来获得对应的奖品ID
        Map<Integer,Integer> shuffleStrategyAwardSearchRateTable = new LinkedHashMap<>();
        for (int i = 0; i < strategyAwardSearchRateTables.size(); i++) {
            shuffleStrategyAwardSearchRateTable.put(i,strategyAwardSearchRateTables.get(i));
        }
        //8.存到redis中
        strategyRepository.storeStrategyAwardSearchRateTable(strategyId,strategyAwardSearchRateTables.size(),shuffleStrategyAwardSearchRateTable);
        
        return true;
    }
    
    @Override
    public Integer getRandomAwardId(Long strategyId) {
        //获取策略对应的结果范围
        int rateRange = strategyRepository.getRateRange(strategyId);
        //返回奖品id
        return strategyRepository.getStrategyAwardAssemble(strategyId,new SecureRandom().nextInt(rateRange));
    }
}
