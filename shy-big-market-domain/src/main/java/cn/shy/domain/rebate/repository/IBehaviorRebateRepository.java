package cn.shy.domain.rebate.repository;

import cn.shy.domain.rebate.model.aggregate.BehaviorRebateAggregate;
import cn.shy.domain.rebate.model.entity.BehaviorRebateOrderEntity;
import cn.shy.domain.rebate.model.valobj.BehaviorTypeVO;
import cn.shy.domain.rebate.model.valobj.DailyBehaviorRebateVO;

import java.util.List;

/**
 * 用户行为返利仓储接口
 * @author shy
 * @since 2024/5/1 21:41
 */
public interface IBehaviorRebateRepository {
    
    /**
     * 查询行为配置对象
     * @param behaviorTypeVO
     * @return
     */
    List<DailyBehaviorRebateVO> queryDailyBehaviorRebateConfig(BehaviorTypeVO behaviorTypeVO);
    
    /**
     * 保存用户返利记录
     * @param userId
     * @param behaviorRebateAggregates
     */
    void saveUserRebateRecord(String userId, List<BehaviorRebateAggregate> behaviorRebateAggregates);
    
    /**
     *
     * @param userId
     * @param outBusinessNo
     * @return
     */
    List<BehaviorRebateOrderEntity> queryOrderByOutBusinessNo(String userId, String outBusinessNo);
}
