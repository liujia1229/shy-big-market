package cn.shy.infrastructure.persistent.dao;

import cn.shy.infrastructure.persistent.po.Award;
import cn.shy.infrastructure.persistent.po.StrategyAward;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @description 奖品表DAO
 * @create 2023-12-16 13:23
 */
@Mapper
public interface IAwardDao {
    
    /**
     * 查询奖品列表
     * @return
     */
    List<Award> queryAwardList();
    
    /**
     * 根据奖品id查询奖品配置
     * @param awardId
     * @return
     */
    String queryAwardConfigByAwardId(Integer awardId);
    
    /**
     * 根据奖品id查询对应的key
     * @param awardId
     * @return
     */
    String queryAwardKey(Integer awardId);
}
