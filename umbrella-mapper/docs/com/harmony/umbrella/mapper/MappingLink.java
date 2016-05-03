package com.harmony.umbrella.mapper;

/**
 * @author wuxii@foxmail.com
 */
public interface MappingLink {

	Mappable getSourceMappable();

	Mappable getTargetMappable();

	boolean isValid();

}
