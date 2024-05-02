package cn.shy.infrastructure.persistent.dao;

import cn.shy.infrastructure.persistent.po.DailyBehaviorRebate;
import com.sun.org.glassfish.gmbal.ManagedObject;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author shy
 * @since 2024/5/1 21:09
 */
@Mapper
public interface IDailyBehaviorRebateDao {
    List<DailyBehaviorRebate> queryDailyBehaviorRebateByBehaviorType(String code);
}
