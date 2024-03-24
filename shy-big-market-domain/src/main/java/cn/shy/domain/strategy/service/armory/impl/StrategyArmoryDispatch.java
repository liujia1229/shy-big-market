package cn.shy.domain.strategy.service.armory.impl;

import cn.shy.domain.strategy.model.entity.StrategyAwardEntity;
import cn.shy.domain.strategy.model.entity.StrategyEntity;
import cn.shy.domain.strategy.model.entity.StrategyRuleEntity;
import cn.shy.domain.strategy.repository.IStrategyRepository;
import cn.shy.domain.strategy.service.armory.IStrategyArmory;
import cn.shy.domain.strategy.service.armory.IStrategyDispatch;
import cn.shy.types.enums.ResponseCode;
import cn.shy.types.exception.AppException;
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
public class StrategyArmoryDispatch implements IStrategyArmory, IStrategyDispatch {
    
    @Resource
    private IStrategyRepository strategyRepository;
    
    @Override
    public boolean assembleLotteryStrategy(Long strategyId) {
        //1.查询策略配置
        List<StrategyAwardEntity> strategyAwardEntityList = strategyRepository.queryStrategyAwardList(strategyId);
        assembleLotteryStrategy(String.valueOf(strategyId),strategyAwardEntityList);
        
        //2.权重策略装配
        StrategyEntity strategyEntity = strategyRepository.queryStrategyEntityByStrategyId(strategyId);
        String ruleWeight = strategyEntity.getRuleWeight();
        if (ruleWeight == null){
            return true;
        }
        StrategyRuleEntity strategyRule = strategyRepository.queryStrategyRuleEntities(strategyId,ruleWeight);
        if (strategyRule == null){
            throw new AppException(ResponseCode.STRATEGY_RULE_WEIGHT_IS_NULL.getCode(), ResponseCode.STRATEGY_RULE_WEIGHT_IS_NULL.getInfo());
        }
        Map<String, List<Integer>> ruleWeightValueMap = strategyRule.getRuleWeightValues();
        Set<String> keys = ruleWeightValueMap.keySet();
        for (String key : keys) {
            List<Integer> ruleWeightValues = ruleWeightValueMap.get(key);
            List<StrategyAwardEntity> strategyAwardEntityClone = new ArrayList<>(strategyAwardEntityList);
            strategyAwardEntityClone.removeIf(removeKey ->
                !ruleWeightValues.contains(removeKey.getAwardId())
            );
            assembleLotteryStrategy(String.valueOf(strategyId).concat("_").concat(key),strategyAwardEntityClone);
        }
        
        return true;
    }
    
    private void assembleLotteryStrategy(String key,List<StrategyAwardEntity> strategyAwardEntityList) {
        
        //1.获取最小概率
        BigDecimal minAwardRate = strategyAwardEntityList.stream()
                .map(StrategyAwardEntity::getAwardRate)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        
        //2.获取概率总和
        BigDecimal rateTotal = strategyAwardEntityList.stream()
                .map(StrategyAwardEntity::getAwardRate)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        //3.用 1 % 0.0001 获得概率范围，百分位、千分位、万分位
        BigDecimal rateRange = rateTotal.divide(minAwardRate,0, RoundingMode.CEILING);
        
        //4. 生成策略奖品概率查找表「这里指需要在list集合中，存放上对应的奖品占位即可，占位越多等于概率越高」
        List<Integer> strategyAwardSearchRateTables = new ArrayList<>(rateRange.intValue());
        for (StrategyAwardEntity strategyAwardEntity : strategyAwardEntityList) {
            Integer awardId = strategyAwardEntity.getAwardId();
            BigDecimal awardRate = strategyAwardEntity.getAwardRate();
            for (int i = 0; i < rateRange.multiply(awardRate).setScale(0, RoundingMode.CEILING).intValue(); i++) {
                strategyAwardSearchRateTables.add(awardId);
            }
        }
        
        //5.乱序
        Collections.shuffle(strategyAwardSearchRateTables);
        
        //6.映射为对应的map key值，对应的就是后续的概率值。通过概率来获得对应的奖品ID
        Map<Integer,Integer> shuffleStrategyAwardSearchRateTable = new LinkedHashMap<>();
        for (int i = 0; i < strategyAwardSearchRateTables.size(); i++) {
            shuffleStrategyAwardSearchRateTable.put(i,strategyAwardSearchRateTables.get(i));
        }
        //8.存到redis中
        strategyRepository.storeStrategyAwardSearchRateTable(key,strategyAwardSearchRateTables.size(),shuffleStrategyAwardSearchRateTable);
        
    }
    @Override
    public Integer getRandomAwardId(Long strategyId) {
        //获取策略对应的结果范围
        int rateRange = strategyRepository.getRateRange(String.valueOf(strategyId));
        //返回奖品id
        return strategyRepository.getStrategyAwardAssemble(String.valueOf(strategyId),new SecureRandom().nextInt(rateRange));
    }
    
    @Override
    public Integer getRandomAwardId(Long strategyId, String ruleWeightValue) {
        String key = String.valueOf(strategyId).concat("_").concat(ruleWeightValue);
        //获取策略对应的结果范围
        int rateRange = strategyRepository.getRateRange(key);
        //返回奖品id
        return strategyRepository.getStrategyAwardAssemble(key,new SecureRandom().nextInt(rateRange));
    }
}
