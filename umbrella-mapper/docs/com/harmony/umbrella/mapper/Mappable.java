package com.harmony.umbrella.mapper;

/**
 * 标识可映射的对象
 * 
 * @author wuxii@foxmail.com
 */
public interface Mappable {

	/**
	 * 映射的名称，Id作用
	 * 
	 * @return
	 */
	String getMappedName();

	/**
	 * 映射的类
	 * 
	 * @return
	 */
	Class<?> getMappedClass();

}
