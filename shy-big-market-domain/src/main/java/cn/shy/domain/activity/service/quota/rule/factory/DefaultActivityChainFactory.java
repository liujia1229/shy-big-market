package cn.shy.domain.activity.service.quota.rule.factory;

import cn.shy.domain.activity.service.quota.rule.IActionChain;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 责任链工厂
 * @author shy
 * @since 2024/4/1 21:22
 */
@Service
public class DefaultActivityChainFactory {

    private IActionChain actionChain;
    
    public DefaultActivityChainFactory(Map<String,IActionChain> actionChainGroup){
        actionChain = actionChainGroup.get(ActionModel.activity_base_action.getCode());
        actionChain.appendNext(actionChainGroup.get(ActionModel.activity_sku_stock_action.getCode()));
    }
    
    public IActionChain openActionChain() {
        return this.actionChain;
    }
    
    @Getter
    @AllArgsConstructor
    public enum ActionModel {
        
        activity_base_action("activity_base_action", "活动的库存、时间校验"),
        activity_sku_stock_action("activity_sku_stock_action", "活动sku库存"),
        ;
        
        private final String code;
        private final String info;
        
    }
}
