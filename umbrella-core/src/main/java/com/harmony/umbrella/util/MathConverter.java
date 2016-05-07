package com.harmony.umbrella.util;

import java.math.BigDecimal;

/**
 * 数字计算的时候的帮助工具
 * 
 * @author wuxii@foxmail.com
 */
public interface MathConverter extends Converter<Number, BigDecimal> {

    /**
     * 计算前对数字处理, 如:可以将数字精确
     * 
     * @param number
     *            待处理的数字
     * @return 处理后的数字
     */
    BigDecimal covertBefore(Number number);

    /**
     * 对计算后的得数进行处理.
     * 
     * @param number
     *            待处理的数字
     * @return 处理后的数字
     */
    BigDecimal convertResult(Number number);

    /**
     * 对数字计算的最后的数进行处理.
     * 
     * @param number
     *            待处理的数字
     * @return 处理后的数字
     */
    BigDecimal convertLast(Number number);

}
