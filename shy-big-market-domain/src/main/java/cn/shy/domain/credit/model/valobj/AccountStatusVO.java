package cn.shy.domain.credit.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author shy
 * @since 2024/7/28 15:08
 */
@AllArgsConstructor
@Getter
public enum AccountStatusVO {
    open("open", "开启"),
    close("close", "冻结"),
    ;
    
    private final String code;
    private final String desc;
}
