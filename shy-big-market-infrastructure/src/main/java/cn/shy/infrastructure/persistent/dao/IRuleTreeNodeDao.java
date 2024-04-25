package cn.shy.infrastructure.persistent.dao;

import cn.shy.infrastructure.persistent.po.RuleTreeNode;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author shy
 * @since 2024/3/28 21:29
 */
@Mapper
public interface IRuleTreeNodeDao {
    List<RuleTreeNode> queryRuleTreeNodeListByTreeId(String treeId);
    
    List<RuleTreeNode> queryRuleLocks(String[] treeIds);
}
