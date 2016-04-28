package com.harmony.umbrella.access;

/**
 * thread safe
 * 
 * @author wuxii@foxmail.com
 */
public interface Access {

    Class<?> getType(String name, Object target);

    boolean isAccessible(String name, Object target);

    Object get(String name, Object target);

    void set(String name, Object target, Object value);

}
