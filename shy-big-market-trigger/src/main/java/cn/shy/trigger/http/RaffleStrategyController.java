package cn.shy.trigger.http;

import cn.shy.domain.activity.service.IRaffleActivityAccountQuotaService;
import cn.shy.domain.strategy.model.entity.RaffleAwardEntity;
import cn.shy.domain.strategy.model.entity.RaffleFactorEntity;
import cn.shy.domain.strategy.model.entity.StrategyAwardEntity;
import cn.shy.domain.strategy.model.valobj.RuleWeightVO;
import cn.shy.domain.strategy.service.IRaffleAward;
import cn.shy.domain.strategy.service.IRaffleRule;
import cn.shy.domain.strategy.service.IRaffleStrategy;
import cn.shy.domain.strategy.service.armory.IStrategyArmory;
import cn.shy.trigger.api.IRaffleStrategyService;
import cn.shy.trigger.api.dto.*;
import cn.shy.types.enums.ResponseCode;
import cn.shy.types.exception.AppException;
import cn.shy.types.model.Response;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 营销抽奖服务
 *
 * @author shy
 * @since 2024/3/30 16:26
 */
@RestController()
@Slf4j
@RequestMapping("/api/${app.config.api-version}/raffle/strategy")
@CrossOrigin("${app.config.cross-origin}")
public class RaffleStrategyController implements IRaffleStrategyService {
    
    @Resource
    private IStrategyArmory strategyArmory;
    
    @Resource
    private IRaffleAward raffleAward;
    
    @Resource
    private IRaffleStrategy raffleStrategy;
    
    @Resource
    private IRaffleRule raffleRule;
    
    @Resource
    private IRaffleActivityAccountQuotaService raffleActivityAccountQuotaService;
    
    
    @GetMapping("strategy_armory")
    @Override
    public Response<Boolean> strategyArmory(Long strategyId) {
        try {
            log.info("抽奖策略装配开始 strategyId：{}", strategyId);
            boolean isFlag = strategyArmory.assembleLotteryStrategy(strategyId);
            
            Response<Boolean> response = Response.<Boolean>builder()
                    .info(ResponseCode.SUCCESS.getInfo())
                    .code(ResponseCode.SUCCESS.getCode())
                    .data(isFlag)
                    .build();
            log.info("抽奖策略装配完成 strategyId：{} response: {}", strategyId, JSON.toJSONString(response));
            
            return response;
        } catch (Exception e) {
            log.error("抽奖策略装配失败 strategyId：{}", strategyId, e);
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }
    
    @PostMapping("query_raffle_award_list")
    @Override
    public Response<List<RaffleAwardListResponseDTO>> queryRaffleAwardList(@RequestBody RaffleAwardListRequestDTO request) {
        try {
            log.info("查询抽奖奖品列表配开始 userId:{} activityId：{}", request.getUserId(), request.getActivityId());
            //1.参数校验
            if (StringUtils.isEmpty(request.getUserId()) || request.getActivityId() == null) {
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
            }
            //2.查询奖品配置
            List<StrategyAwardEntity> strategyAwardEntities = raffleAward.queryRaffleStrategyAwardListByActivityId(request.getActivityId());
            
            //3.获取规则配置
            String[] treeIds = strategyAwardEntities.stream()
                    .map(StrategyAwardEntity::getRuleModels)
                    .filter(ruleModel -> ruleModel != null && ruleModel.isEmpty())
                    .toArray(String[]::new);
            // 4. 查询规则配置 - 获取奖品的解锁限制，抽奖N次后解锁
            Map<String, Integer> ruleLockCountMap = raffleRule.queryAwardRuleLockCount(treeIds);
            // 5. 查询抽奖次数 - 用户已经参与的抽奖次数
            Integer dayPartakeCount = raffleActivityAccountQuotaService.queryRaffleActivityAccountDayPartakeCount(request.getActivityId(), request.getUserId());
            
            List<RaffleAwardListResponseDTO> raffleAwardListResponseDTOS = new ArrayList<>(strategyAwardEntities.size());
            for (StrategyAwardEntity strategyAward : strategyAwardEntities) {
                Integer awardRuleLockCount = ruleLockCountMap.get(strategyAward.getRuleModels());
                raffleAwardListResponseDTOS.add(RaffleAwardListResponseDTO.builder()
                        .awardId(strategyAward.getAwardId())
                        .awardTitle(strategyAward.getAwardTitle())
                        .awardSubtitle(strategyAward.getAwardSubtitle())
                        .sort(strategyAward.getSort())
                        .awardRuleLockCount(awardRuleLockCount)
                        .isAwardUnlock(awardRuleLockCount == null || dayPartakeCount >= awardRuleLockCount)
                        .waitUnlockCount(awardRuleLockCount == null || awardRuleLockCount <= dayPartakeCount ? 0 : awardRuleLockCount - dayPartakeCount)
                        .build());
            }
            Response<List<RaffleAwardListResponseDTO>> response = Response.<List<RaffleAwardListResponseDTO>>builder()
                    .data(raffleAwardListResponseDTOS)
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .build();
            
            log.info("查询抽奖奖品列表配置完成 userId:{} activityId：{} response: {}", request.getUserId(), request.getActivityId(), JSON.toJSONString(response));
            return response;
        } catch (Exception e) {
            log.error("查询抽奖奖品列表配置失败 userId:{} activityId：{}", request.getUserId(), request.getActivityId(), e);
            return Response.<List<RaffleAwardListResponseDTO>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }
    
    @PostMapping("random_raffle")
    @Override
    public Response<RaffleResponseDTO> randomRaffle(@RequestBody RaffleRequestDTO requestDTO) {
        try {
            log.info("随机抽奖开始 strategyId: {}", requestDTO.getStrategyId());
            //调用抽奖
            RaffleAwardEntity raffleAwardEntity = raffleStrategy.performRaffle(RaffleFactorEntity.builder()
                    .userId("system")
                    .strategyId(requestDTO.getStrategyId())
                    .build());
            //封装结果返回
            Response<RaffleResponseDTO> response = Response.<RaffleResponseDTO>builder()
                    .data(RaffleResponseDTO.builder()
                            .awardId(raffleAwardEntity.getAwardId())
                            .awardIndex(raffleAwardEntity.getSort())
                            .build())
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .build();
            log.info("随机抽奖完成 strategyId: {} response: {}", requestDTO.getStrategyId(), JSON.toJSONString(response));
            return response;
        } catch (Exception e) {
            log.error("随机抽奖失败 strategyId：{}", requestDTO.getStrategyId(), e);
            return Response.<RaffleResponseDTO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }
    
    @PostMapping("query_raffle_strategy_rule_weight")
    @Override
    public Response<List<RaffleStrategyRuleWeightResponseDTO>> queryRaffleStrategyRuleWeight(RaffleStrategyRuleWeightRequestDTO requestDTO) {
        try {
            log.info("查询抽奖策略权重规则配置开始 userId:{} activityId：{}", requestDTO.getUserId(), requestDTO.getActivityId());
            //1.参数校验
            if (StringUtils.isBlank(requestDTO.getUserId()) || null == requestDTO.getActivityId()) {
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
            }
            //2.用户额度
            Integer userActivityAccountTotalUseCount = raffleActivityAccountQuotaService.queryRaffleActivityAccountDayPartakeCount(requestDTO.getActivityId(), requestDTO.getUserId());
            
            //3.策略查询
            List<RuleWeightVO> ruleWeightVOList = raffleRule.queryAwardRuleWeightByActivityId(requestDTO.getActivityId());
            List<RaffleStrategyRuleWeightResponseDTO> strategyRuleWeightList = new ArrayList<>();
            
            for (RuleWeightVO ruleWeightVO : ruleWeightVOList) {
                List<RuleWeightVO.Award> awardList = ruleWeightVO.getAwardList();
                List<RaffleStrategyRuleWeightResponseDTO.StrategyAward> strategyAwardList = new ArrayList<>(awardList.size());
                for (RuleWeightVO.Award award : awardList) {
                    strategyAwardList.add(RaffleStrategyRuleWeightResponseDTO.StrategyAward.builder()
                            .awardTitle(award.getAwardTitle())
                            .awardId(award.getAwardId())
                            .build());
                }
                RaffleStrategyRuleWeightResponseDTO raffleStrategyRuleWeightResponseDTO = new RaffleStrategyRuleWeightResponseDTO();
                raffleStrategyRuleWeightResponseDTO.setStrategyAwards(strategyAwardList);
                raffleStrategyRuleWeightResponseDTO.setUserActivityAccountTotalUseCount(userActivityAccountTotalUseCount);
                raffleStrategyRuleWeightResponseDTO.setRuleWeightCount(ruleWeightVO.getWeight());
                
                strategyRuleWeightList.add(raffleStrategyRuleWeightResponseDTO);
            }
            Response<List<RaffleStrategyRuleWeightResponseDTO>> response = Response.<List<RaffleStrategyRuleWeightResponseDTO>>builder()
                    .data(strategyRuleWeightList)
                    .info(ResponseCode.SUCCESS.getInfo())
                    .code(ResponseCode.SUCCESS.getCode())
                    .build();
            log.info("查询抽奖策略权重规则配置完成 userId:{} activityId：{} response: {}", requestDTO.getUserId(), requestDTO.getActivityId(), JSON.toJSONString(response));
            return response;
        } catch (Exception e) {
            log.error("查询抽奖策略权重规则配置失败 userId:{} activityId：{}", requestDTO.getUserId(), requestDTO.getActivityId(), e);
            return Response.<List<RaffleStrategyRuleWeightResponseDTO>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }
}
