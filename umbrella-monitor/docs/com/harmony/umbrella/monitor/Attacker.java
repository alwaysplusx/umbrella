package com.harmony.umbrella.monitor;

import java.util.Map;

/**
 * 获取监控对象的内部属性
 * 
 * @author wuxii@foxmail.com
 */
public interface Attacker<T> {

    /**
     * 获取内部数据
     * 
     * @param target
     *            目标监控对象
     * @param names
     *            内部对象名称. 如: 字段名称, 方法名称
     * @return 内部对象的键值对
     */
    Map<String, Object> attack(T target, String... names);

}
