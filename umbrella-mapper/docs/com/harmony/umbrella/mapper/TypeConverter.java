package com.harmony.umbrella.mapper;

/**
 * @author wuxii@foxmail.com
 */
public interface TypeConverter {

	Object converter(Object sourceValue, Object targetValue, Class<?> sourceClass, Class<?> targetClass);

}
