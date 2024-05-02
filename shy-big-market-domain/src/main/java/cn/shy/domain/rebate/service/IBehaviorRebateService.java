package cn.shy.domain.rebate.service;

import cn.shy.domain.rebate.model.entity.BehaviorEntity;

import java.util.List;

/**
 * 行为返利服务接口
 * @author shy
 * @since 2024/5/1 21:28
 */
public interface IBehaviorRebateService {
    
    List<String> createOrder(BehaviorEntity behaviorEntity);

}
