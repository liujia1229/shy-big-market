package cn.shy.domain.activity.service.partake;

import cn.shy.domain.activity.model.aggregate.CreatePartakeOrderAggregate;
import cn.shy.domain.activity.model.entity.ActivityEntity;
import cn.shy.domain.activity.model.entity.PartakeRaffleActivityEntity;
import cn.shy.domain.activity.model.entity.UserRaffleOrderEntity;
import cn.shy.domain.activity.model.valobj.ActivityStateVO;
import cn.shy.domain.activity.repository.IActivityRepository;
import cn.shy.domain.activity.service.IRaffleActivityPartakeService;
import cn.shy.types.enums.ResponseCode;
import cn.shy.types.exception.AppException;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author shy
 * @since 2024/4/6 14:51
 */
@Slf4j
public abstract class AbstractRaffleActivityPartake implements IRaffleActivityPartakeService {
    
    @Resource
    protected IActivityRepository activityRepository;
    
    @Override
    public UserRaffleOrderEntity createOrder(String userId, Long activityId) {
        return this.createOrder(PartakeRaffleActivityEntity.builder()
                .activityId(activityId)
                .userId(userId)
                .build());
    }
    
    @Override
    public UserRaffleOrderEntity createOrder(PartakeRaffleActivityEntity partakeRaffleActivityEntity) {
        //1.基础信息
        String userId = partakeRaffleActivityEntity.getUserId();
        Long activityId = partakeRaffleActivityEntity.getActivityId();
        Date currentDate = new Date();
        
        //2.1查询活动
        ActivityEntity activityEntity = activityRepository.queryRaffleActivityByActivityId(activityId);
        
        //2.2校验活动状态
        if (!ActivityStateVO.open.equals(activityEntity.getState())) {
            throw new AppException(ResponseCode.ACTIVITY_STATE_ERROR.getCode(), ResponseCode.ACTIVITY_STATE_ERROR.getInfo());
        }
        //2.3校验活动时间
        if (currentDate.after(activityEntity.getEndDateTime()) || currentDate.before(activityEntity.getBeginDateTime())) {
            throw new AppException(ResponseCode.ACTIVITY_DATE_ERROR.getCode(), ResponseCode.ACTIVITY_DATE_ERROR.getInfo());
        }
        
        //3.查询当前用户未使用的订单,如果有:直接返回该未使用订单
        UserRaffleOrderEntity userRaffleOrderEntity = activityRepository.queryNoUsedRaffleOrder(partakeRaffleActivityEntity);
        if (userRaffleOrderEntity != null) {
            log.info("创建参与活动订单 userId:{} activityId:{} userRaffleOrderEntity:{}", userId, activityId, JSON.toJSONString(userRaffleOrderEntity));
            return userRaffleOrderEntity;
        }
        
        //4.没有未使用订单,创建新订单:额度账户过滤&返回账户构建对象
        CreatePartakeOrderAggregate createPartakeOrderAggregate = this.doFilterAccount(userId, activityId, currentDate);
        
        //5.构建订单
        UserRaffleOrderEntity userRaffleOrder = this.buildUserRaffleOrder(userId, activityId, currentDate);
        
        
        //6.填充订单实体对象
        createPartakeOrderAggregate.setUserRaffleOrderEntity(userRaffleOrder);
        
        //7.保存聚合对象
        activityRepository.saveCreatePartakeOrderAggregate(createPartakeOrderAggregate);
        
        return userRaffleOrder;
    }
    
    protected abstract UserRaffleOrderEntity buildUserRaffleOrder(String userId, Long activityId, Date currentDate);
    
    protected abstract CreatePartakeOrderAggregate doFilterAccount(String userId, Long activityId, Date currentDate);
}
