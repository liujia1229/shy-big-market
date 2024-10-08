package cn.shy.types.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

/**
 * 随机数生成工具类
 * @author shy
 * @since 2024/10/4 20:19
 */
public class RandomUtils {
    
    /**
     * 生成min-max范围之间的数,精度为小数点后scale
     * @param min
     * @param max
     * @param scale
     * @return
     */
    public static BigDecimal getRandomBigDecimal(BigDecimal min, BigDecimal max, int scale) {
        // 检查 min 是否大于 max
        if (min.compareTo(max) > 0) {
            throw new IllegalArgumentException("min 必须小于或等于 max");
        }
        
        // 计算 max 和 min 之间的差值 (range = max - min)
        BigDecimal range = max.subtract(min);
        
        // 创建随机数生成器
        Random random = new Random();
        
        // 生成 0 到 1 之间的随机浮点数，然后乘以范围
        BigDecimal randomFactor = new BigDecimal(random.nextDouble());
        
        // 生成的随机数在 [0, range] 之间
        BigDecimal randomInRange = randomFactor.multiply(range);
        
        // 将随机数加到 min 上，并按指定小数位舍入
        BigDecimal result = randomInRange.add(min).setScale(scale, RoundingMode.HALF_UP);
        
        return result;
    }

}
