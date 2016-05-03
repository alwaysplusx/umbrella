package com.harmony.umbrella.util;

/**
 * @author wuxii@foxmail.com、
 */
public interface Converter<T, V> {

    V convert(T t);

}
