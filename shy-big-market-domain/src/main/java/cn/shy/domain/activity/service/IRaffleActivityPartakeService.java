package cn.shy.domain.activity.service;

import cn.shy.domain.activity.model.entity.PartakeRaffleActivityEntity;
import cn.shy.domain.activity.model.entity.UserRaffleOrderEntity;

/**
 * 抽奖活动参与服务
 * @author shy
 * @since 2024/4/6 14:46
 */
public interface IRaffleActivityPartakeService {
    
    /**
     * 创建抽奖单；用户参与抽奖活动，扣减活动账户库存，产生抽奖单。如存在未被使用的抽奖单则直接返回已存在的抽奖单。
     * @param partakeRaffleActivityEntity
     * @return
     */
    UserRaffleOrderEntity createOrder(PartakeRaffleActivityEntity partakeRaffleActivityEntity);

}
