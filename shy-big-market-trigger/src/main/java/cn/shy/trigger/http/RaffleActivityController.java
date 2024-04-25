package cn.shy.trigger.http;

import cn.shy.domain.activity.model.entity.UserRaffleOrderEntity;
import cn.shy.domain.activity.service.IRaffleActivityPartakeService;
import cn.shy.domain.activity.service.armory.ActivityArmory;
import cn.shy.domain.activity.service.armory.IActivityArmory;
import cn.shy.domain.award.model.entity.UserAwardRecordEntity;
import cn.shy.domain.award.model.valobj.AwardStateVO;
import cn.shy.domain.award.service.IAwardService;
import cn.shy.domain.strategy.model.entity.RaffleAwardEntity;
import cn.shy.domain.strategy.model.entity.RaffleFactorEntity;
import cn.shy.domain.strategy.service.IRaffleStrategy;
import cn.shy.domain.strategy.service.armory.IStrategyArmory;
import cn.shy.trigger.api.IRaffleActivityService;
import cn.shy.trigger.api.dto.ActivityDrawRequestDTO;
import cn.shy.trigger.api.dto.ActivityDrawResponseDTO;
import cn.shy.types.enums.ResponseCode;
import cn.shy.types.exception.AppException;
import cn.shy.types.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 抽奖活动服务 注意；在不引用 application/case 层的时候，就需要让接口实现层来做领域的串联。一些较大规模的系统，需要加入 case 层。
 *
 * @author shy
 * @since 2024/4/21 14:10
 */
@RestController
@Slf4j
@RequestMapping("/api/${app.config.api-version}/raffle/activity/")
@CrossOrigin("${app.config.cross-origin}")
public class RaffleActivityController implements IRaffleActivityService {
    
    @Resource
    private IActivityArmory activityArmory;
    
    @Resource
    private IStrategyArmory strategyArmory;
    
    @Resource
    private IRaffleStrategy raffleStrategy;
    
    @Resource
    private IAwardService awardService;
    
    @Resource
    private IRaffleActivityPartakeService raffleActivityPartakeService;
    
    @Override
    @GetMapping("armory")
    public Response<Boolean> armory(@RequestParam Long activityId) {
        try {
            log.info("活动装配，数据预热，开始 activityId:{}", activityId);
            //活动装配
            activityArmory.assembleActivitySkuByActivityId(activityId);
            //策略装配
            strategyArmory.assembleLotteryStrategyByActivityId(activityId);
            log.info("活动装配，数据预热，完成 activityId:{}", activityId);
            return Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(true)
                    .build();
        } catch (Exception e) {
            log.error("活动装配，数据预热，失败 activityId:{}", activityId, e);
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }
    
    /**
     * 抽奖接口
     *
     * @param request 请求对象
     * @return 抽奖结果
     * <p>
     * 接口：<a href="http://localhost:8091/api/v1/raffle/activity/draw">/api/v1/raffle/activity/draw</a>
     * 入参：{"activityId":100001,"userId":"xiaofuge"}
     * <p>
     * curl --request POST \
     * --url http://localhost:8091/api/v1/raffle/activity/draw \
     * --header 'content-type: application/json' \
     * --data '{
     * "userId":"xiaofuge",
     * "activityId": 100301
     * }'
     */
    @PostMapping("draw")
    @Override
    public Response<ActivityDrawResponseDTO> draw(@RequestBody ActivityDrawRequestDTO request) {
        try {
            //参数校验
            log.info("活动抽奖 userId:{} activityId:{}", request.getUserId(), request.getActivityId());
            if (StringUtils.isBlank(request.getUserId()) || request.getActivityId() == null) {
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
            }
            //参与活动-创建参与记录订单
            UserRaffleOrderEntity orderEntity = raffleActivityPartakeService.createOrder(request.getUserId(), request.getActivityId());
            log.info("活动抽奖，创建订单 userId:{} activityId:{} orderId:{}", request.getUserId(), request.getActivityId(), orderEntity.getOrderId());
            //抽奖策略-执行抽奖
            RaffleAwardEntity raffleAwardEntity = raffleStrategy.performRaffle(RaffleFactorEntity.builder()
                    .userId(request.getUserId())
                    .strategyId(orderEntity.getStrategyId())
                    .endDateTime(orderEntity.getEndDateTime())
                    .build());
            //存放结果-写入中奖记录
            UserAwardRecordEntity userAwardRecordEntity = UserAwardRecordEntity.builder()
                    .userId(orderEntity.getUserId())
                    .activityId(orderEntity.getActivityId())
                    .strategyId(orderEntity.getStrategyId())
                    .orderId(orderEntity.getOrderId())
                    .awardId(raffleAwardEntity.getAwardId())
                    .awardTitle(raffleAwardEntity.getAwardTitle())
                    .awardTime(new Date())
                    .awardState(AwardStateVO.create)
                    .build();
            awardService.saveUserAwardRecord(userAwardRecordEntity);
            //返回结果
            return Response.<ActivityDrawResponseDTO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(ActivityDrawResponseDTO.builder()
                            .awardIndex(raffleAwardEntity.getSort())
                            .awardTitle(raffleAwardEntity.getAwardTitle())
                            .awardId(raffleAwardEntity.getAwardId())
                            .build())
                    .build();
            
        } catch (AppException e) {
            log.error("活动抽奖失败 userId:{} activityId:{}", request.getUserId(), request.getActivityId(), e);
            return Response.<ActivityDrawResponseDTO>builder()
                    .info(e.getInfo())
                    .code(e.getCode())
                    .build();
        } catch (Exception e) {
            log.error("活动抽奖失败 userId:{} activityId:{}", request.getUserId(), request.getActivityId(), e);
            return Response.<ActivityDrawResponseDTO>builder()
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .code(ResponseCode.UN_ERROR.getCode())
                    .build();
        }
    }
}
