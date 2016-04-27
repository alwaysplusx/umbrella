package com.harmony.umbrella.core.accessor;

/**
 * @author wuxii@foxmail.com
 */
public interface Accessor {

    Class<?> getType(String name, Object target);

    boolean isAccessible(String name, Object target);

    Object get(String name, Object target);

    void set(String name, Object target, Object value);

}
