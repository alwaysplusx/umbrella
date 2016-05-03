package com.harmony.umbrella.mapper.resolver;

import com.harmony.umbrella.mapper.metadata.ClassMappable;

/**
 * @author wuxii@foxmail.com
 */
public interface MappableResolver {

	ClassMappable resolve(Class<?> clazz);

}
