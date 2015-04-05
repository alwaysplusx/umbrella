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
package com.harmony.modules.jaxws.util;

import java.lang.reflect.Method;
import java.util.Map;

import com.harmony.modules.invoker.InvokException;
import com.harmony.modules.invoker.Invoker;
import com.harmony.modules.jaxws.Phase;

/**
 * 为{@linkplain com.harmony.modules.jaxws.Handler.HandleMethod}提供的invoker
 * @author wuxii@foxmail.com
 */
public interface HandleMethodInvoker extends Invoker {

	/**
	 * 执行handleMethod
	 * 
	 * @param target 目标实例
	 * @param args 参数
	 * @return
	 * @throws InvokException
	 */
	Object invokeHandleMethod(Object target, Object[] args) throws InvokException;

	/**
	 * 执行handleMethod
	 * 
	 * @param target 目标实例
	 * @param args 参数
	 * @param contextMap
	 * @return
	 * @throws InvokException
	 */
	Object invokeHandleMethod(Object target, Object[] args, Map<String, String> contextMap) throws InvokException;

	/**
	 * handler class
	 */
	Class<?> getHandlerClass();

	/**
	 * handleMethod
	 */
	Method getHandleMethod();

	/**
	 * 对应的执行周期
	 */
	Phase getPhase();

	/**
	 * 最后的参数是否要带上ContextMap
	 */
	boolean isEndWithMap();

	/**
	 * 设置异常
	 * @param throwable
	 */
	void setThrowable(Throwable throwable);

	/**
	 * 设置结果
	 * @param result
	 */
	void setResult(Object result);

	/**
	 * 设置contextMap
	 * @param contextMap
	 */
	void setContextMap(Map<String, String> contextMap);

}
