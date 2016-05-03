package com.harmony.umbrella.mapper.metadata;

import java.lang.reflect.Method;

import com.harmony.umbrella.mapper.Mappable;

/**
 * 所有符合javaBean的getter， setter将被解析为{@linkplain RowMappable}.<p> 可以表示为： <li>一个getter方法
 * <li>一个setter方法 <li>一个字段的getter与setter的组合
 * 
 * @author wuxii@foxmail.com
 */
public interface RowMappable extends Mappable {

	/**
	 * getter方法
	 * 
	 * @return
	 */
	Method getReadMethod();

	/**
	 * setter方法
	 * 
	 * @return
	 */
	Method getWriteMethod();

	/**
	 * 对应getter方法的返回类型
	 * 
	 * @return
	 */
	Class<?> getType();

	/**
	 * 存在getter方法则可读
	 * 
	 * @return
	 */
	boolean readable();

	/**
	 * 存在setter方法则可写
	 * 
	 * @return
	 */
	boolean writable();

	/**
	 * 拥有该{@linkplain RowMappable}的{@linkplain ClassMappable}
	 * 
	 * @return
	 */
	ClassMappable getOwner();

}
