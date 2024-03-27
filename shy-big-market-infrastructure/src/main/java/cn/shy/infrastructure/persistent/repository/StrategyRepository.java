package cn.shy.infrastructure.persistent.repository;

import cn.shy.domain.strategy.model.entity.StrategyAwardEntity;
import cn.shy.domain.strategy.model.entity.StrategyEntity;
import cn.shy.domain.strategy.model.entity.StrategyRuleEntity;
import cn.shy.domain.strategy.model.valobj.StrategyAwardRuleModelVO;
import cn.shy.domain.strategy.repository.IStrategyRepository;
import cn.shy.infrastructure.persistent.dao.IAwardDao;
import cn.shy.infrastructure.persistent.dao.IStrategyAwardDao;
import cn.shy.infrastructure.persistent.dao.IStrategyDao;
import cn.shy.infrastructure.persistent.dao.IStrategyRuleDao;
import cn.shy.infrastructure.persistent.po.Strategy;
import cn.shy.infrastructure.persistent.po.StrategyAward;
import cn.shy.infrastructure.persistent.po.StrategyRule;
import cn.shy.infrastructure.persistent.redis.RedissonService;
import cn.shy.types.common.Constants;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static cn.shy.types.common.Constants.RedisKey.*;

/**
 * @author shy
 * @since 2024/3/23 22:21
 */
@Repository
public class StrategyRepository implements IStrategyRepository {
    
    
    @Resource
    private RedissonService redissonService;
    
    @Resource
    private IAwardDao awardDao;
    
    @Resource
    private IStrategyDao strategyDao;
    
    @Resource
    private IStrategyRuleDao strategyRuleDao;
    
    @Resource
    private IStrategyAwardDao strategyAwardDao;
    
    
    
    @Override
    public List<StrategyAwardEntity> queryStrategyAwardList(Long strategyId) {
        //先从缓存中差
        String cacheKey = STRATEGY_AWARD_KEY + strategyId;
        List<StrategyAwardEntity> strategyAwardEntities = redissonService.getValue(cacheKey);
        if (strategyAwardEntities != null && !strategyAwardEntities.isEmpty()){
            return strategyAwardEntities;
        }
        //查数据库
        List<StrategyAward> strategyAwards = awardDao.queryAwardListByStrategyId(strategyId);
        strategyAwardEntities = new ArrayList<>(strategyAwards.size());
        for (StrategyAward strategyAward : strategyAwards) {
            StrategyAwardEntity strategyAwardEntity = StrategyAwardEntity.builder()
                    .strategyId(strategyAward.getStrategyId())
                    .awardId(strategyAward.getAwardId())
                    .awardCount(strategyAward.getAwardCount())
                    .awardCountSurplus(strategyAward.getAwardCountSurplus())
                    .awardRate(strategyAward.getAwardRate())
                    .build();
            strategyAwardEntities.add(strategyAwardEntity);
        }
        redissonService.setValue(cacheKey,strategyAwardEntities);
        return strategyAwardEntities;
    }
    
    @Override
    public void storeStrategyAwardSearchRateTable(String key, int rateRange, Map<Integer, Integer> shuffleStrategyAwardSearchRateTable) {
        //存储概率范围
        redissonService.setValue(STRATEGY_RATE_RANGE_KEY + key,rateRange);
        //存储概率查找表
        Map<Integer,Integer> cacheRateTable = redissonService.getMap(STRATEGY_RATE_TABLE_KEY + key);
        cacheRateTable.putAll(shuffleStrategyAwardSearchRateTable);
    }
    
    @Override
    public int getRateRange(String strategyId) {
        return redissonService.getValue(STRATEGY_RATE_RANGE_KEY + strategyId);
    }
    
    @Override
    public Integer getStrategyAwardAssemble(String key, int randomKey) {
        return redissonService.getFromMap(STRATEGY_RATE_TABLE_KEY + key,randomKey);
    }
    
    @Override
    public StrategyEntity queryStrategyEntityByStrategyId(Long strategyId) {
        //先取缓存
        String cacheKey = Constants.RedisKey.STRATEGY_KEY + strategyId;
        StrategyEntity strategyEntity = redissonService.getValue(cacheKey);
        if (strategyEntity == null){
            Strategy strategy = strategyDao.queryStrategyEntityByStrategyId(strategyId);
            strategyEntity = StrategyEntity.builder()
                    .strategyId(strategy.getStrategyId())
                    .strategyDesc(strategy.getStrategyDesc())
                    .ruleModels(strategy.getRuleModels())
                    .build();
        }
        return strategyEntity;
    }
    
    @Override
    public StrategyRuleEntity queryStrategyRuleEntity(Long strategyId, String ruleModel) {
        StrategyRule strategyRuleReq = new StrategyRule();
        strategyRuleReq.setStrategyId(strategyId);
        strategyRuleReq.setRuleModel(ruleModel);
        
        StrategyRule strategyRule = strategyRuleDao.queryStrategyRule(strategyRuleReq);
        
        return StrategyRuleEntity.builder()
                .strategyId(strategyRule.getStrategyId())
                .awardId(strategyRule.getAwardId())
                .ruleType(strategyRule.getRuleType())
                .ruleModel(strategyRule.getRuleModel())
                .ruleValue(strategyRule.getRuleValue())
                .ruleDesc(strategyRule.getRuleDesc())
                .build();
    }
    
    @Override
    public String queryStrategyRuleValue(Long strategyId, Integer awardId, String ruleModel) {
        StrategyRule strategyRule = new StrategyRule();
        strategyRule.setRuleModel(ruleModel);
        strategyRule.setAwardId(awardId);
        strategyRule.setStrategyId(strategyId);
        
        return strategyRuleDao.queryStrategyRuleValue(strategyRule);
    }
    
    @Override
    public StrategyAwardRuleModelVO queryStrategyAwardRuleModelVO(Long strategyId, Integer awardId) {
        StrategyAward strategyAward = new StrategyAward();
        strategyAward.setStrategyId(strategyId);
        strategyAward.setAwardId(awardId);
        
        String ruleModel = strategyAwardDao.queryStrategyAwardRuleModels(strategyAward);
        return StrategyAwardRuleModelVO.builder().ruleModels(ruleModel).build();
    }
    
    
}
