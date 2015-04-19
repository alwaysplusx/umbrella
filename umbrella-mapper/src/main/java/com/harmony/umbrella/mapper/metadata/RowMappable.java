/*
 * Copyright 2013-2015 wuxii@foxmail.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
