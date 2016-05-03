package com.harmony.umbrella.mapper;

/**
 * @author wuxii@foxmail.com
 */
public interface MappingBuilder {

	Object linking(Class<?> source, Class<?> target);

	Object linking(Class<?> source, Class<?> target, MappingVisitor handler);

}
