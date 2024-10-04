package cn.shy.domain.credit.service;

import cn.shy.domain.credit.model.entity.TradeEntity;

/**
 * 用户积分模块
 * @author shy
 * @since 2024/7/28 2:27
 */
public interface ICreditAdjustService {
    
    /**
     * 创建积分订单
     * @param tradeEntity
     * @return 订单id
     */
    String createOder(TradeEntity tradeEntity);
}
