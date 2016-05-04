package com.harmony.umbrella.access;

/**
 * 
 * @author wuxii@foxmail.com
 */
public interface TypedAccessor<T> extends Comparable<TypedAccessor<?>> {

    Class<T> getType();

    Object get(String name, T obj);

    void set(String name, T obj, Object val);

    /**
     * 根据类的继承关系反序排
     */
    @Override
    public int compareTo(TypedAccessor<?> o);

}