package com.harmony.umbrella.mapper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author wuxii@foxmail.com
 */
public interface MappingVisitor {

	boolean visitorType(Class<?> sourceType, Class<?> targetType);

	boolean visitorMethod(Method sourceMethod, Method targetMethod);

	boolean visitorField(Field sourceField, Field targetField);

}
