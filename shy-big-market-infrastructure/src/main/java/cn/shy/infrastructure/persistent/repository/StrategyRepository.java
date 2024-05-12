package cn.shy.infrastructure.persistent.repository;

import cn.shy.domain.strategy.model.entity.StrategyAwardEntity;
import cn.shy.domain.strategy.model.entity.StrategyEntity;
import cn.shy.domain.strategy.model.entity.StrategyRuleEntity;
import cn.shy.domain.strategy.model.valobj.*;
import cn.shy.domain.strategy.repository.IStrategyRepository;
import cn.shy.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import cn.shy.infrastructure.persistent.dao.*;
import cn.shy.infrastructure.persistent.po.*;
import cn.shy.infrastructure.persistent.redis.IRedisService;
import cn.shy.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static cn.shy.types.common.Constants.RedisKey.*;

/**
 * @author shy
 * @since 2024/3/23 22:21
 */
@Repository
@Slf4j
public class StrategyRepository implements IStrategyRepository {
    
    @Resource
    private IStrategyDao strategyDao;
    
    @Resource
    private IStrategyRuleDao strategyRuleDao;
    
    @Resource
    private IStrategyAwardDao strategyAwardDao;
    
    @Resource
    private IRedisService redisService;
    
    @Resource
    private IRuleTreeDao ruleTreeDao;
    
    @Resource
    private IRuleTreeNodeDao ruleTreeNodeDao;
    
    @Resource
    private IRuleTreeNodeLineDao ruleTreeNodeLineDao;
    
    @Resource
    private IRaffleActivityDao raffleActivityDao;
    
    @Resource
    private IRaffleActivityAccountDayDao raffleActivityAccountDayDao;
    
    @Resource
    private IRaffleActivityAccountDao raffleActivityAccountDao;
    
    @Override
    public List<StrategyAwardEntity> queryStrategyAwardList(Long strategyId) {
        //先从缓存中差
        String cacheKey = STRATEGY_AWARD_KEY + strategyId;
        List<StrategyAwardEntity> strategyAwardEntities = redisService.getValue(cacheKey);
        if (strategyAwardEntities != null && !strategyAwardEntities.isEmpty()) {
            return strategyAwardEntities;
        }
        //查数据库
        List<StrategyAward> strategyAwards = strategyAwardDao.queryAwardListByStrategyId(strategyId);
        strategyAwardEntities = new ArrayList<>(strategyAwards.size());
        for (StrategyAward strategyAward : strategyAwards) {
            StrategyAwardEntity strategyAwardEntity = StrategyAwardEntity.builder()
                    .strategyId(strategyAward.getStrategyId())
                    .awardId(strategyAward.getAwardId())
                    .awardTitle(strategyAward.getAwardTitle())
                    .awardSubtitle(strategyAward.getAwardSubtitle())
                    .awardCount(strategyAward.getAwardCount())
                    .awardCountSurplus(strategyAward.getAwardCountSurplus())
                    .awardRate(strategyAward.getAwardRate())
                    .sort(strategyAward.getSort())
                    .build();
            strategyAwardEntities.add(strategyAwardEntity);
        }
        redisService.setValue(cacheKey, strategyAwardEntities);
        return strategyAwardEntities;
    }
    
    @Override
    public void storeStrategyAwardSearchRateTable(String key, int rateRange, Map<Integer, Integer> shuffleStrategyAwardSearchRateTable) {
        //存储概率范围
        redisService.setValue(STRATEGY_RATE_RANGE_KEY + key, rateRange);
        //存储概率查找表
        Map<Integer, Integer> cacheRateTable = redisService.getMap(STRATEGY_RATE_TABLE_KEY + key);
        cacheRateTable.putAll(shuffleStrategyAwardSearchRateTable);
    }
    
    @Override
    public int getRateRange(String strategyId) {
        return redisService.getValue(STRATEGY_RATE_RANGE_KEY + strategyId);
    }
    
    @Override
    public Integer getStrategyAwardAssemble(String key, int randomKey) {
        return redisService.getFromMap(STRATEGY_RATE_TABLE_KEY + key, randomKey);
    }
    
    @Override
    public StrategyEntity queryStrategyEntityByStrategyId(Long strategyId) {
        //先取缓存
        String cacheKey = Constants.RedisKey.STRATEGY_KEY + strategyId;
        StrategyEntity strategyEntity = redisService.getValue(cacheKey);
        if (strategyEntity == null) {
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
    public String queryStrategyRuleValue(Long strategyId, String ruleModel) {
        return this.queryStrategyRuleValue(strategyId, null, ruleModel);
    }
    
    @Override
    public StrategyAwardRuleModelVO queryStrategyAwardRuleModelVO(Long strategyId, Integer awardId) {
        StrategyAward strategyAward = new StrategyAward();
        strategyAward.setStrategyId(strategyId);
        strategyAward.setAwardId(awardId);
        
        String ruleModel = strategyAwardDao.queryStrategyAwardRuleModels(strategyAward);
        return StrategyAwardRuleModelVO.builder().ruleModels(ruleModel).build();
    }
    
    @Override
    public RuleTreeVO queryRuleTreeVOByTreeId(String treeId) {
        // 优先从缓存获取
        String cacheKey = Constants.RedisKey.RULE_TREE_VO_KEY + treeId;
        RuleTreeVO ruleTreeVOCache = redisService.getValue(cacheKey);
        if (null != ruleTreeVOCache) {
            return ruleTreeVOCache;
        }
        
        // 从数据库获取
        RuleTree ruleTree = ruleTreeDao.queryRuleTreeByTreeId(treeId);
        List<RuleTreeNode> ruleTreeNodes = ruleTreeNodeDao.queryRuleTreeNodeListByTreeId(treeId);
        List<RuleTreeNodeLine> ruleTreeNodeLines = ruleTreeNodeLineDao.queryRuleTreeNodeLineListByTreeId(treeId);
        
        // 1. tree node line 转换Map结构
        Map<String, List<RuleTreeNodeLineVO>> ruleTreeNodeLineMap = new HashMap<>();
        for (RuleTreeNodeLine ruleTreeNodeLine : ruleTreeNodeLines) {
            RuleTreeNodeLineVO ruleTreeNodeLineVO = RuleTreeNodeLineVO.builder()
                    .treeId(ruleTreeNodeLine.getTreeId())
                    .ruleNodeFrom(ruleTreeNodeLine.getRuleNodeFrom())
                    .ruleNodeTo(ruleTreeNodeLine.getRuleNodeTo())
                    .ruleLimitType(RuleLimitTypeVO.valueOf(ruleTreeNodeLine.getRuleLimitType()))
                    .ruleLimitValue(RuleLogicCheckTypeVO.valueOf(ruleTreeNodeLine.getRuleLimitValue()))
                    .build();
            
            List<RuleTreeNodeLineVO> ruleTreeNodeLineVOList = ruleTreeNodeLineMap.computeIfAbsent(ruleTreeNodeLine.getRuleNodeFrom(), k -> new ArrayList<>());
            ruleTreeNodeLineVOList.add(ruleTreeNodeLineVO);
        }
        
        // 2. tree node 转换为Map结构
        Map<String, RuleTreeNodeVO> treeNodeMap = new HashMap<>();
        for (RuleTreeNode ruleTreeNode : ruleTreeNodes) {
            RuleTreeNodeVO ruleTreeNodeVO = RuleTreeNodeVO.builder()
                    .treeId(ruleTreeNode.getTreeId())
                    .ruleKey(ruleTreeNode.getRuleKey())
                    .ruleDesc(ruleTreeNode.getRuleDesc())
                    .ruleValue(ruleTreeNode.getRuleValue())
                    .treeNodeLineVOList(ruleTreeNodeLineMap.get(ruleTreeNode.getRuleKey()))
                    .build();
            treeNodeMap.put(ruleTreeNode.getRuleKey(), ruleTreeNodeVO);
        }
        
        // 3. 构建 Rule Tree
        RuleTreeVO ruleTreeVODB = RuleTreeVO.builder()
                .treeId(ruleTree.getTreeId())
                .treeName(ruleTree.getTreeName())
                .treeDesc(ruleTree.getTreeDesc())
                .treeRootRuleNode(ruleTree.getTreeRootRuleKey())
                .treeNodeMap(treeNodeMap)
                .build();
        
        redisService.setValue(cacheKey, ruleTreeVODB);
        return ruleTreeVODB;
    }
    
    @Override
    public void cacheStrategyAwardCount(String cacheKey, Integer awardCount) {
        if (redisService.isExists(cacheKey)) {
            return;
        }
        redisService.setAtomicLong(cacheKey, awardCount);
    }
    
    @Override
    public Boolean subtractionAwardStock(String cacheKey, Date endDateTime) {
        long surplus = redisService.decr(cacheKey);
        if (surplus < 0) {
            redisService.setValue(cacheKey, 0);
            return false;
        }
        // 1. 按照cacheKey decr 后的值，如 99、98、97 和 key 组成为库存锁的key进行使用。
        // 2. 加锁为了兜底，如果后续有恢复库存，手动处理等，也不会超卖。因为所有的可用库存key，都被加锁了。
        String lockKey = cacheKey + Constants.UNDERLINE + surplus;
        Boolean lock = null;
        if (endDateTime == null) {
            lock = redisService.setNx(lockKey);
        } else {
            long expireTime = endDateTime.getTime() - System.currentTimeMillis();
            lock = redisService.setNx(lockKey, expireTime, TimeUnit.MICROSECONDS);
        }
        if (lock) {
            return true;
        }
        log.info("策略奖品库存加锁失败 {}", lockKey);
        return false;
    }
    
    @Override
    public StrategyAwardStockKeyVO takeQueueValue() {
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_COUNT_QUERY_KEY;
        RBlockingQueue<StrategyAwardStockKeyVO> blockingQueue = redisService.getBlockingQueue(cacheKey);
        return blockingQueue.poll();
    }
    
    @Override
    public void awardStockConsumeSendQueue(StrategyAwardStockKeyVO strategyAwardStockKeyVO) {
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_COUNT_QUERY_KEY;
        RBlockingQueue<StrategyAwardStockKeyVO> blockingQueue = redisService.getBlockingQueue(cacheKey);
        RDelayedQueue<StrategyAwardStockKeyVO> delayedQueue = redisService.getDelayedQueue(blockingQueue);
        delayedQueue.offer(strategyAwardStockKeyVO, 3, TimeUnit.SECONDS);
    }
    
    @Override
    public StrategyAwardEntity queryStrategyAwardEntity(Long strategyId, Integer awardId) {
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_KEY + strategyId + Constants.UNDERLINE + awardId;
        StrategyAwardEntity strategyAwardEntity = redisService.getValue(cacheKey);
        if (strategyAwardEntity != null) {
            return strategyAwardEntity;
        }
        StrategyAward strategyAwardReq = new StrategyAward();
        strategyAwardReq.setAwardId(awardId);
        strategyAwardReq.setStrategyId(strategyId);
        
        StrategyAward strategyAwardRes = strategyAwardDao.queryStrategyAward(strategyAwardReq);
        
        strategyAwardEntity = StrategyAwardEntity.builder()
                .strategyId(strategyAwardRes.getStrategyId())
                .awardId(strategyAwardRes.getAwardId())
                .awardTitle(strategyAwardRes.getAwardTitle())
                .awardSubtitle(strategyAwardRes.getAwardSubtitle())
                .awardCount(strategyAwardRes.getAwardCount())
                .awardCountSurplus(strategyAwardRes.getAwardCountSurplus())
                .awardRate(strategyAwardRes.getAwardRate())
                .sort(strategyAwardRes.getSort())
                .build();
        //存缓存
        redisService.setValue(cacheKey, strategyAwardEntity);
        return strategyAwardEntity;
    }
    
    @Override
    public void updateStrategyAwardStock(Long strategyId, Integer awardId) {
        StrategyAward strategyAward = new StrategyAward();
        strategyAward.setStrategyId(strategyId);
        strategyAward.setAwardId(awardId);
        strategyAwardDao.updateStrategyAwardStock(strategyAward);
    }
    
    @Override
    public Long queryStrategyIdByActivityId(Long activityId) {
        return raffleActivityDao.queryStrategyIdByActivityId(activityId);
    }
    
    @Override
    public Integer queryTodayUserRaffleCount(String userId, Long strategyId) {
        //活动id
        Long activityId = raffleActivityDao.queryActivityIdByStrategyId(strategyId);
        //封装参数
        RaffleActivityAccountDay raffleActivityAccountDayReq = new RaffleActivityAccountDay();
        raffleActivityAccountDayReq.setActivityId(activityId);
        raffleActivityAccountDayReq.setDay(RaffleActivityAccountDay.currentDay());
        raffleActivityAccountDayReq.setUserId(userId);
        
        RaffleActivityAccountDay raffleActivityAccountDay = raffleActivityAccountDayDao.queryActivityAccountDayByUserId(raffleActivityAccountDayReq);
        if (raffleActivityAccountDay == null) {
            return 0;
        }
        return raffleActivityAccountDay.getDayCount() - raffleActivityAccountDay.getDayCountSurplus();
    }
    
    @Override
    public Map<String, Integer> queryAwardRuleLockCount(String[] treeIds) {
        if (treeIds == null || treeIds.length == 0) {
            return new HashMap<>(0);
        }
        List<RuleTreeNode> ruleTreeNodes = ruleTreeNodeDao.queryRuleLocks(treeIds);
        Map<String, Integer> resultMap = new HashMap<>(ruleTreeNodes.size());
        for (RuleTreeNode ruleTreeNode : ruleTreeNodes) {
            resultMap.put(ruleTreeNode.getTreeId(), Integer.valueOf(ruleTreeNode.getRuleValue()));
        }
        return resultMap;
    }
    
    
    @Override
    public List<RuleWeightVO> queryAwardRuleWeight(Long strategyId) {
        String redisKey = Constants.RedisKey.STRATEGY_RULE_WEIGHT_KEY + strategyId;
        List<RuleWeightVO> ruleWeightVOList = redisService.getValue(redisKey);
        if (ruleWeightVOList != null && !ruleWeightVOList.isEmpty()) {
            return ruleWeightVOList;
        }
        
        ruleWeightVOList = new ArrayList<>();
        
        //1.查询ruleValue
        StrategyRule strategyRuleReq = new StrategyRule();
        strategyRuleReq.setStrategyId(strategyId);
        strategyRuleReq.setRuleModel(DefaultChainFactory.LogicModel.RULE_WEIGHT.getCode());
        String ruleValue = strategyRuleDao.queryStrategyRuleValue(strategyRuleReq);
        //2.借助实体对象解析ruleValue
        StrategyRuleEntity strategyRuleEntity = new StrategyRuleEntity();
        strategyRuleEntity.setRuleModel(DefaultChainFactory.LogicModel.RULE_WEIGHT.getCode());
        strategyRuleEntity.setRuleValue(ruleValue);
        Map<String, List<Integer>> ruleWeightValues = strategyRuleEntity.getRuleWeightValues();
        Set<String> ruleWeightKeys = ruleWeightValues.keySet();
        //3.遍历组装奖品配置
        for (String ruleWeightKey : ruleWeightKeys) {
            List<Integer> awardIds = ruleWeightValues.get(ruleWeightKey);
            List<RuleWeightVO.Award> awardList = new ArrayList<>();
            for (Integer awardId : awardIds) {
                StrategyAward strategyAwardReq = new StrategyAward();
                strategyAwardReq.setAwardId(awardId);
                strategyAwardReq.setStrategyId(strategyId);
                StrategyAward strategyAward = strategyAwardDao.queryStrategyAward(strategyAwardReq);
                awardList.add(RuleWeightVO.Award.builder()
                        .awardId(strategyAward.getAwardId())
                        .awardTitle(strategyAward.getAwardTitle())
                        .build());
            }
            
            RuleWeightVO ruleWeightVO = new RuleWeightVO();
            ruleWeightVO.setAwardIds(awardIds);
            ruleWeightVO.setAwardList(awardList);
            ruleWeightVO.setWeight(Integer.valueOf(ruleValue.split(Constants.COLON)[0]));
            ruleWeightVO.setRuleValue(ruleValue);
            
            ruleWeightVOList.add(ruleWeightVO);
        }
        // 设置缓存 - 实际场景中，这类数据，可以在活动下架的时候统一清空缓存。
        
        redisService.setValue(redisKey, ruleWeightVOList);
        return ruleWeightVOList;
    }
    
    @Override
    public Integer queryActivityAccountTotalUseCount(String userId, Long strategyId) {
        Long activityId = raffleActivityDao.queryActivityIdByStrategyId(strategyId);
        RaffleActivityAccount raffleActivityAccountReq = new RaffleActivityAccount();
        raffleActivityAccountReq.setUserId(userId);
        raffleActivityAccountReq.setActivityId(activityId);
        RaffleActivityAccount raffleActivityAccount = raffleActivityAccountDao.queryActivityAccountByUserId(raffleActivityAccountReq);
        return raffleActivityAccount.getTotalCount() - raffleActivityAccount.getTotalCountSurplus();
    }
}
