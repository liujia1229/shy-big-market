package cn.shy.trigger.api;

import cn.shy.trigger.api.dto.RaffleAwardListRequestDTO;
import cn.shy.trigger.api.dto.RaffleAwardListResponseDTO;
import cn.shy.trigger.api.dto.RaffleRequestDTO;
import cn.shy.trigger.api.dto.RaffleResponseDTO;
import cn.shy.types.model.Response;

import java.util.List;

/**
 * @author shy
 * @since 2024/3/30 16:26
 */
public interface IRaffleService {
    
    /**
     * 策略装配接口
     * @param strategyId
     * @return
     */
    Response<Boolean> strategyArmory(Long strategyId);
    
    /**
     * 查询奖品列表
     * @param requestDTO
     * @return
     */
    Response<List<RaffleAwardListResponseDTO>> queryRaffleAwardList(RaffleAwardListRequestDTO requestDTO);
    
    
    /**
     * 随机抽奖接口
     * @param requestDTO
     * @return
     */
    Response<RaffleResponseDTO> randomRaffle(RaffleRequestDTO requestDTO);
    
}
