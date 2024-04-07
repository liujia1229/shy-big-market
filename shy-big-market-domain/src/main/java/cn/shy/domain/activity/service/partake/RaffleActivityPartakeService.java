package cn.shy.domain.activity.service.partake;

import cn.shy.domain.activity.model.aggregate.CreatePartakeOrderAggregate;
import cn.shy.domain.activity.model.entity.*;
import cn.shy.domain.activity.model.valobj.UserRaffleOrderStateVO;
import cn.shy.types.enums.ResponseCode;
import cn.shy.types.exception.AppException;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author shy
 * @since 2024/4/6 14:52
 */
@Service
public class RaffleActivityPartakeService extends AbstractRaffleActivityPartake{
    
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy-MM");
    
    @Override
    protected CreatePartakeOrderAggregate doFilterAccount(String userId, Long activityId, Date currentDate) {
        //查询总额度
        ActivityAccountEntity activityAccountEntity = activityRepository.queryActivityAccountByUserId(userId,activityId);
        //判断总额度
        if (activityAccountEntity == null || activityAccountEntity.getTotalCountSurplus() <= 0){
            throw new AppException(ResponseCode.ACCOUNT_QUOTA_ERROR.getCode(), ResponseCode.ACCOUNT_QUOTA_ERROR.getInfo());
        }
        String day = dateFormat.format(currentDate);

        String month = monthFormat.format(currentDate);
        
        //查询月额度 如果没有则创建
        ActivityAccountMonthEntity activityAccountMonthEntity = activityRepository.queryActivityAccountMonthByUserId(userId,activityId,month);
        if (activityAccountMonthEntity != null && activityAccountMonthEntity.getMonthCountSurplus() <= 0){
            throw new AppException(ResponseCode.ACCOUNT_MONTH_QUOTA_ERROR.getCode(), ResponseCode.ACCOUNT_MONTH_QUOTA_ERROR.getInfo());
        }
        boolean isExistAccountMonth = activityAccountMonthEntity != null;
        if (activityAccountMonthEntity == null){
            activityAccountMonthEntity = new ActivityAccountMonthEntity();
            activityAccountMonthEntity.setUserId(userId);
            activityAccountMonthEntity.setActivityId(activityId);
            activityAccountMonthEntity.setMonthCount(activityAccountEntity.getMonthCount());
            activityAccountMonthEntity.setMonth(month);
            activityAccountMonthEntity.setMonthCountSurplus(activityAccountEntity.getMonthCountSurplus());
        }
        
        
        //查询日额度 如果没有则创建
        ActivityAccountDayEntity activityAccountDayEntity = activityRepository.queryActivityAccountDayByUserId(userId,activityId,day);
        if (activityAccountDayEntity != null && activityAccountDayEntity.getDayCountSurplus() <= 0){
            throw new AppException(ResponseCode.ACCOUNT_DAY_QUOTA_ERROR.getCode(), ResponseCode.ACCOUNT_DAY_QUOTA_ERROR.getInfo());
        }
        boolean isExistAccountDay = activityAccountDayEntity != null;
        if (activityAccountDayEntity == null){
            activityAccountDayEntity = new ActivityAccountDayEntity();
            activityAccountDayEntity.setActivityId(activityId);
            activityAccountDayEntity.setDayCount(activityAccountEntity.getDayCount());
            activityAccountDayEntity.setDay(day);
            activityAccountDayEntity.setUserId(userId);
            activityAccountDayEntity.setDayCountSurplus(activityAccountEntity.getDayCountSurplus());
        }
        
        //构建对象
        
        return CreatePartakeOrderAggregate.builder()
                .isExistAccountMonth(isExistAccountMonth)
                .isExistAccountDay(isExistAccountDay)
                .activityAccountEntity(activityAccountEntity)
                .activityAccountMonthEntity(activityAccountMonthEntity)
                .activityAccountDayEntity(activityAccountDayEntity)
                .userId(userId)
                .activityId(activityId)
                .build();
    }
    
    @Override
    protected UserRaffleOrderEntity buildUserRaffleOrder(String userId, Long activityId, Date currentDate) {
        ActivityEntity activityEntity = activityRepository.queryRaffleActivityByActivityId(activityId);
        
        UserRaffleOrderEntity userRaffleOrderEntity = new UserRaffleOrderEntity();
        userRaffleOrderEntity.setActivityId(activityId);
        userRaffleOrderEntity.setOrderTime(currentDate);
        userRaffleOrderEntity.setUserId(userId);
        userRaffleOrderEntity.setActivityName(activityEntity.getActivityName());
        userRaffleOrderEntity.setOrderId(RandomStringUtils.randomNumeric(12));
        userRaffleOrderEntity.setStrategyId(activityEntity.getStrategyId());
        userRaffleOrderEntity.setOrderState(UserRaffleOrderStateVO.create);
        return userRaffleOrderEntity;
    }
}
