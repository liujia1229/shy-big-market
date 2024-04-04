package cn.shy.types.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 事件基础抽象类
 * @author shy
 * @since 2024/4/3 15:29
 */
@Data
public abstract class BaseEvent<T> {

    public abstract String topic();
    
    public abstract EventMessage<T> buildEventMessage(T data);
    
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EventMessage<T>{
        private String id;
        private Date timestamp;
        private T data;
    }
}
