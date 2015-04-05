/*
 * Copyright 2002-2014 the original author or authors.
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
package com.harmony.modules.jaxws;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Map;

/**
 * 执行时候的上下文
 * <p>
 * 上下文中包括:
 * <li>待执行的web service接口
 * <li>待执行的方法
 * <li>执行时候所需要用到的参数
 * <li>调用地址
 * <li>用户名密码
 * <li>以及其他待扩展的属性
 * 
 * @author wuxii@foxmail.com
 *
 */
public interface JaxWsContext extends Serializable {

	/**
	 * context中username的检索键
	 */
	String USERNAME = "jaxws.username";
	/**
	 * context中password的检索键
	 */
	String PASSWORD = "jaxws.password";

	/**
	 * 接口类
	 * 
	 * @return 接口
	 */
	Class<?> getServiceInterface();

	/**
	 * 方法参数
	 * 
	 * @return 参数
	 */
	Object[] getParameters();

	/**
	 * 所要执行的方法
	 * 
	 * @return 方法名
	 */
	String getMethodName();

	/**
	 * 访问地址
	 * 
	 * @return 地址
	 */
	String getAddress();

	/**
	 * 在context上下文通过{@link JaxWsContext#USERNAME}获得
	 * 
	 * @return 用户名
	 */
	String getUsername();

	/**
	 * 在context上下文通过{@link JaxWsContext#PASSWORD}获得
	 * 
	 * @return 密码
	 */
	String getPassword();

	/**
	 * 在上下文中获取属性值
	 * 
	 * @param contextKey
	 *            检索键
	 * @return 上下文中的值
	 */
	String get(String contextKey);

	/**
	 * 上下文中是否包含检索条件
	 * 
	 * @param contextKey
	 * @return true 包含， false 不包含
	 */
	boolean contains(String contextKey);

	/**
	 * 所要执行的接口方法
	 * 
	 * @return 接口方法
	 * @throws NoSuchMethodException
	 *             该接口中不存在该方法
	 */
	Method getMethod() throws NoSuchMethodException;

	/**
	 * 上下文中的head名称
	 * 
	 * @return
	 */
	Enumeration<String> getContextHeaderNames();

	/**
	 * 当前执行环境的Context, 返回的ContextMap与原有脱离. 修改作为返回值的Map不会对当前context照成影响
	 * 
	 * @return Map
	 */
	Map<String, String> getContextMap();

}
