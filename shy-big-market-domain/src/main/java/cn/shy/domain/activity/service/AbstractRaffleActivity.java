package cn.shy.domain.activity.service;

import cn.shy.domain.activity.model.entity.*;
import cn.shy.domain.activity.repository.IActivityRepository;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * 抽奖活动抽象类，定义标准的流程
 *
 * @author shy
 * @since 2024/4/1 15:35
 */
@Slf4j
public abstract class AbstractRaffleActivity implements IRaffleOrder {
    
    @Resource
    protected IActivityRepository activityRepository;
    
    @Override
    public ActivityOrderEntity createRaffleActivityOrder(ActivityShopCartEntity activityShopCartEntity) {
        //1.查询sku实体类
        ActivitySkuEntity activitySkuEntity = activityRepository.queryActivitySku(activityShopCartEntity.getSku());
        //2.查询活动实体类
        ActivityEntity activityEntity = activityRepository.queryRaffleActivityByActivityId(activitySkuEntity.getActivityId());
        //3.查询次数信息
        ActivityCountEntity activityCountEntity = activityRepository.queryRaffleActivityCountByActivityCountId(activityEntity.getActivityCountId());
        log.info("查询结果：{} {} {}", JSON.toJSONString(activitySkuEntity), JSON.toJSONString(activityEntity), JSON.toJSONString(activityCountEntity));
        
        return ActivityOrderEntity.builder().build();
    }
}
