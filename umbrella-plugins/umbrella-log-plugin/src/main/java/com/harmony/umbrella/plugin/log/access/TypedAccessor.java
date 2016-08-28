package com.harmony.umbrella.plugin.log.access;

/**
 * 类型解析工具
 * 
 * @author wuxii@foxmail.com
 */
public interface TypedAccessor<T> {

    /**
     * 所支持的解析类型
     * 
     * @return 支持的类型
     */
    Class<T> getType();

    /**
     * 通过名称获取target object中的内容
     * 
     * @param name
     *            属性名
     * @param obj
     *            需要抓取的内容的拥有对象
     * @return 属性名对应的内容
     */
    Object get(String name, T obj);

    /**
     * 通过属性名称设置target的值
     * 
     * @param name
     *            属性名称
     * @param obj
     *            设置值的目标
     * @param val
     *            属性对应的值
     */
    void set(String name, T obj, Object val);

}