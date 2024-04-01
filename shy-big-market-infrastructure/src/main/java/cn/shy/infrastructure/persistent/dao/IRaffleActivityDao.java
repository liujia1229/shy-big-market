package cn.shy.infrastructure.persistent.dao;

import cn.shy.infrastructure.persistent.po.RaffleActivity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author shy
 * @since 2024/3/31 20:52
 */
@Mapper
public interface IRaffleActivityDao {
    
    RaffleActivity queryRaffleActivityByActivityId(Long activityId);
    
}
