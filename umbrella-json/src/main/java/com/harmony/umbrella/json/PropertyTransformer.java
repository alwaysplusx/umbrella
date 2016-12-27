package com.harmony.umbrella.json;

import java.util.Collection;

/**
 * 类型属性转化器
 * 
 * @author wuxii@foxmail.com
 */
public interface PropertyTransformer extends Cloneable {

    /**
     * 检测是否支持对应的类型
     * 
     * @param type
     *            类型
     * @return 是否支持标志
     */
    boolean support(Class<?> type);

    /**
     * 将对应的属性转化为pattern
     * 
     * @param property
     *            属性名称或pattern
     * @return 被转化后的pattern
     */
    Collection<String> transform(Class<?> type, String... property);

    Object clone();

}
