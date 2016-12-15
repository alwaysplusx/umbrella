package com.harmony.umbrella.data.util;

/**
 * 类型属性转化器
 * <p>
 * 如:
 * <ul>
 * <li>page下需要过滤的为: content[*].propertyName
 * <li>list/array下需要过滤的为: [*].propertyName
 * <li>单个实体需过滤的为: propertyName
 * </ul>
 * 
 * @author wuxii@foxmail.com
 */
public interface TypedPropertyTransformer {

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
    String[] transform(String... property);

}
