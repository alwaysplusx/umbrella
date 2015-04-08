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
package com.harmony.modules.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author wuxii@foxmail.com
 */
public class MethodCaller {

	@SuppressWarnings("unused")
	private final static Class<?>[] EMPTY_PARAM = new Class[0];

	public static Object invokeMethod(Object target, String methodName, Object... args) throws NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		return null;
	}

	public static Object invokeMethod(Object target, Method method, Object... args) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		return method.invoke(target, args);
	}

	public static Object invokeMethodWithUncheckException(Object target, Method method, Object... args) {
		Exception ex = null;
		try {
			return method.invoke(target, args);
		} catch (IllegalAccessException e) {
			ex = e;
		} catch (InvocationTargetException e) {
			ex = e;
		}
		throw Exceptions.unchecked(ex);
	}
}
