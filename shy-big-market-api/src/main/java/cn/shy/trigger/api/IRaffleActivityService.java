package cn.shy.trigger.api;

import cn.shy.trigger.api.dto.ActivityDrawRequestDTO;
import cn.shy.trigger.api.dto.ActivityDrawResponseDTO;
import cn.shy.types.model.Response;

/**
 * 抽奖活动服务
 * @author shy
 * @since 2024/4/20 22:05
 */
public interface IRaffleActivityService {
    /**
     * 活动预热缓存
     * @param activityId
     * @return
     */
    Response<Boolean> armory(Long activityId);
    
    /**
     * 活动抽奖接口
     * @param requestDTO
     * @return
     */
    Response<ActivityDrawResponseDTO> draw(ActivityDrawRequestDTO requestDTO);
}
