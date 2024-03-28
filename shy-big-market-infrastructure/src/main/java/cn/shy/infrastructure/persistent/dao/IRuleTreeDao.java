package cn.shy.infrastructure.persistent.dao;

import cn.shy.infrastructure.persistent.po.RuleTree;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author shy
 * @since 2024/3/28 21:30
 */
@Mapper
public interface IRuleTreeDao {
    RuleTree queryRuleTreeByTreeId(String treeId);
    
}
