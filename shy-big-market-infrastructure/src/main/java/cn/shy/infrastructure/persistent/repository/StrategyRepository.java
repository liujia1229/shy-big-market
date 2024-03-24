package cn.shy.infrastructure.persistent.repository;

import cn.shy.domain.strategy.model.entity.StrategyAwardEntity;
import cn.shy.domain.strategy.repository.IStrategyRepository;
import cn.shy.infrastructure.persistent.dao.IAwardDao;
import cn.shy.infrastructure.persistent.po.StrategyAward;
import cn.shy.infrastructure.persistent.redis.RedissonService;
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
    public void storeStrategyAwardSearchRateTable(Long strategyId, int rateRange, Map<Integer, Integer> shuffleStrategyAwardSearchRateTable) {
        //存储概率范围
        redissonService.setValue(STRATEGY_RATE_RANGE_KEY + strategyId,rateRange);
        //存储概率查找表
        Map<Integer,Integer> cacheRateTable = redissonService.getMap(STRATEGY_RATE_TABLE_KEY + strategyId);
        cacheRateTable.putAll(shuffleStrategyAwardSearchRateTable);
    }
    
    @Override
    public int getRateRange(Long strategyId) {
        return redissonService.getValue(STRATEGY_RATE_RANGE_KEY + strategyId);
    }
    
    @Override
    public Integer getStrategyAwardAssemble(Long strategyId, int randomKey) {
        return redissonService.getFromMap(STRATEGY_RATE_TABLE_KEY + strategyId,randomKey);
    }
    
    
}
