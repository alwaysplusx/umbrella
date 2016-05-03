package com.harmony.umbrella.mapper.metadata;

import java.util.Set;

import com.harmony.umbrella.mapper.Mappable;

/**
 * 类映射元数据
 * 
 * @author wuxii@foxmail.com
 */
public interface ClassMappable extends Mappable {

	/**
	 * 类名
	 * 
	 * @see com.harmony.umbrella.mapper.Mappable#getMappedName()
	 * 
	 * @see Class#getName()
	 */
	String getMappedName();

	/**
	 * 类中所对应可以被映射的字段
	 * 
	 * @return
	 */
	Set<RowMappable> getRowMappables();

}
