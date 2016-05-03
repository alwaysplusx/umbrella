package com.harmony.umbrella.mapper;

import java.util.Set;

/**
 * @author wuxii@foxmail.com
 */
public interface Mapper {

	void map(Object source, Object target);

	void map(Object source, Object target, Set<MappingLink> links);

}
