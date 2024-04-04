package cn.shy.domain.activity.service.rule.impl;

import cn.shy.domain.activity.model.entity.ActivityCountEntity;
import cn.shy.domain.activity.model.entity.ActivityEntity;
import cn.shy.domain.activity.model.entity.ActivitySkuEntity;
import cn.shy.domain.activity.model.valobj.ActivityStateVO;
import cn.shy.domain.activity.service.rule.AbstractActionChain;
import cn.shy.domain.activity.service.rule.IActionChain;
import cn.shy.types.enums.ResponseCode;
import cn.shy.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 活动规则过滤【日期、状态】
 * @author shy
 * @since 2024/4/1 21:20
 */
@Slf4j
@Component("activity_base_action")
public class ActivityBaseActionChain extends AbstractActionChain {
    @Override
    public boolean action(ActivitySkuEntity activitySkuEntity, ActivityEntity activityEntity, ActivityCountEntity activityCountEntity) {
        log.info("活动责任链-基础信息【有效期、状态、库存(sku)】校验开始。sku:{} activityId:{}", activitySkuEntity.getSku(), activityEntity.getActivityId());
        //1.校验活动状态
        if (!ActivityStateVO.open.equals(activityEntity.getState())){
            throw new AppException(ResponseCode.ACTIVITY_STATE_ERROR.getCode(), ResponseCode.ACTIVITY_STATE_ERROR.getInfo());
        }
        //2.校验活动时间
        Date currentDate = new Date();
        if(currentDate.after(activityEntity.getEndDateTime()) || currentDate.before(activityEntity.getBeginDateTime())){
            throw new AppException(ResponseCode.ACTIVITY_DATE_ERROR.getCode(), ResponseCode.ACTIVITY_DATE_ERROR.getInfo());
        }
        //3.校验sku剩余库存
        if (activitySkuEntity.getStockCountSurplus() <= 0){
            throw new AppException(ResponseCode.ACTIVITY_SKU_STOCK_ERROR.getCode(), ResponseCode.ACTIVITY_SKU_STOCK_ERROR.getInfo());
        }
        return this.next().action(activitySkuEntity,activityEntity,activityCountEntity);
    }
}
