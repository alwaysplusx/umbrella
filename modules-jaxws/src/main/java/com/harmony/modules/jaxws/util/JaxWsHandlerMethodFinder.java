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

import static com.harmony.modules.utils.ClassUtils.*;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.harmony.modules.io.utils.ResourceScaner;
import com.harmony.modules.jaxws.Handler;
import com.harmony.modules.jaxws.Handler.HandleMethod;
import com.harmony.modules.jaxws.JaxWsAbortException;
import com.harmony.modules.jaxws.Phase;
import com.harmony.modules.utils.ClassFilter;

/**
 * @author wuxii@foxmail.com
 */
public class JaxWsHandlerMethodFinder {

	private static final String ALL_HANDLER_CLASS = Handler.class.getName();
	@SuppressWarnings("rawtypes")
	private Map<String, Set> handlerCache = new HashMap<String, Set>();
	private final String basePackage;

	public JaxWsHandlerMethodFinder(String basePackage) {
		this.basePackage = basePackage;
	}

	/**
	 * basePackage为当前classpath
	 */
	public JaxWsHandlerMethodFinder() {
		this("");
	}

	/**
	 * basePackage下的所有带有annotation的{@linkplain Handler}, 并{@linkplain Handler}中{@linkplain Handler#value()}
	 * {@linkplain Handler#handles()}不全为空的class
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Class<?>[] getAllHandlerClass() {
		if (handlerCache.containsKey(ALL_HANDLER_CLASS)) {
			Set<Class<?>> classes = (Set<Class<?>>) handlerCache.get(ALL_HANDLER_CLASS);
			return classes.toArray(new Class[classes.size()]);
		}
		Set<Class<?>> handlerClasses = new LinkedHashSet<Class<?>>();
		try {
			Class<?>[] classes = ResourceScaner.getInstance().scanPackage(basePackage, new ClassFilter() {
				@Override
				public boolean accept(Class<?> clazz) {
					try {
						if (clazz.isInterface())
							return false;
						if (clazz.getModifiers() != Modifier.PUBLIC)
							return false;
						if (clazz.getAnnotation(Handler.class) == null)
							return false;
						Handler handler = clazz.getAnnotation(Handler.class);
						if (handler.value().length == 0 && handler.handles().length == 0)
							return false;
						return true;
					} catch (Throwable e) {
						return false;
					}
				}
			});
			if (classes.length > 0) {
				Collections.addAll(handlerClasses, classes);
				handlerCache.put(ALL_HANDLER_CLASS, handlerClasses);
			}
		} catch (IOException e) {
		}
		return handlerClasses.toArray(new Class[handlerClasses.size()]);
	}

	/**
	 * basePackage下所有{@linkplain Handler}的{@linkplain Handler#value()}或者{@linkplain Handler#handles()}值包含serviceClass的类
	 * @param serviceClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Class<?>[] findHandlerClass(Class<?> serviceClass) {
		String cacheKey = cacheKey(serviceClass, null);
		Set<Class<?>> classes = handlerCache.get(cacheKey);
		if (classes == null) {
			classes = new LinkedHashSet<Class<?>>();
			for (Class<?> clazz : getAllHandlerClass()) {
				Handler handler = clazz.getAnnotation(Handler.class);
				if (isMatchHandler(serviceClass, handler))
					classes.add(clazz);
			}
			handlerCache.put(cacheKey, classes);
		}
		return classes.toArray(new Class[classes.size()]);
	}

	/**
	 * 判断Handler中handles或value中是否有serviceClass
	 */
	private boolean isMatchHandler(Class<?> serviceClass, Handler handler) {
		Class<?>[] classes = handler.value();
		for (Class<?> clazz : classes) {
			if (equalsIgnoreClassLoader(serviceClass, clazz))
				return true;
		}
		String[] handlers = handler.handles();
		for (String h : handlers) {
			if (serviceClass.getName().equals(h))
				return true;
		}
		return false;
	}

	/**
	 * {@linkplain #basePackage}下符合serviceMethod周期为Phase的处理方法
	 * 
	 * @param serviceMethod 当前执行的方法
	 * @param phase 执行周期
	 * @return
	 */
	public HandleMethodInvoker[] findHandleMethods(Method serviceMethod, Phase phase) {
		List<HandleMethodInvoker> result = new LinkedList<HandleMethodInvoker>();
		for (Class<?> clazz : getAllHandlerClass()) {
			Collections.addAll(result, findHandleMethods(serviceMethod, clazz, phase));
		}
		return result.toArray(new HandleMethodInvoker[result.size()]);
	}

	/**
	 * handlerClass中符合serviceMethod周期为Phase的处理方法
	 * @param serviceMethod
	 * @param handlerClass
	 * @param phase
	 * @return
	 */
	protected HandleMethodInvoker[] findHandleMethods(Method serviceMethod, Class<?> handlerClass, Phase phase) {
		List<HandleMethodInvoker> result = new LinkedList<HandleMethodInvoker>();
		String name = serviceMethod.getName();
		for (Method handleMethod : handlerClass.getMethods()) {
			if (handleMethod.getDeclaringClass().equals(Object.class))
				continue;
			HandleMethod ann = handleMethod.getAnnotation(HandleMethod.class);
			// annotation不为空、周期相同、方法名与annotation相同或与handleMethod相同
			if (ann != null && phase.equals(ann.phase()) && (ann.methodName().equals(name) || handleMethod.getName().equals(name)) && isMatchHandleMethod(serviceMethod, handleMethod, phase)) {
				HandleMethodReflectInvoker hmi = new HandleMethodReflectInvoker(handlerClass, handleMethod, phase);
				Class<?>[] parameterTypes = handleMethod.getParameterTypes();
				hmi.setEndWithMap(equalsIgnoreClassLoader(Map.class, parameterTypes[parameterTypes.length - 1]));
				result.add(hmi);
			}
		}
		return result.toArray(new HandleMethodInvoker[result.size()]);
	}

	/**
	 * 主要检查handleMethod对应的方法参数是否符合serviceMethod的方法参数要求.
	 * <p>handlerMethod可以在最后添加一个Map参数(非必要)
	 *   <p>各个Phase的方法参数要求说明：
	 *   <pre>
	 *  {@linkplain Phase#PRE_INVOKE}:可以为serviceMethod的方法参数，各个参数类型可以为serviceMethod参数的父类. 
	 *  在所有参数最后可以添加Map表示Context，但这个参数是非必要的。
	 *  
	 *  {@linkplain Phase#ABORT}:
	 *  </pre>
	 * @param serviceMethod
	 * @param handleMethod
	 * @param phase
	 * @return
	 * @see HandleMethod
	 */
	protected boolean isMatchHandleMethod(Method serviceMethod, Method handleMethod, Phase phase) {
		// 接口的方法参数类型
		final Class<?>[] serviceParamTypes = serviceMethod.getParameterTypes();
		// handler的方法参数类型
		final Class<?>[] handleParamTypes = handleMethod.getParameterTypes();
		List<Class<?>> types = new LinkedList<Class<?>>();
		Collections.addAll(types, serviceParamTypes);
		switch (phase) {
		case PRE_INVOKE:
			if (!typeEquals(serviceParamTypes, handleParamTypes)) {
				types.add(Map.class);
				return typeEquals(types.toArray(new Class[types.size()]), handleParamTypes);
			}
			return true;
		case ABORT:
			types.add(0, JaxWsAbortException.class);
			if (!typeEquals(types.toArray(new Class[types.size()]), handleParamTypes)) {
				types.add(Map.class);
				return typeEquals(types.toArray(new Class[types.size()]), handleParamTypes);
			}
			return true;
		case POST_INVOKE:
			types.add(0, serviceMethod.getReturnType());
			if (!typeEquals(types.toArray(new Class[types.size()]), handleParamTypes)) {
				types.add(Map.class);
				return typeEquals(types.toArray(new Class[types.size()]), handleParamTypes);
			}
			return true;
		case THROWING:
			types.add(0, Exception.class);
			if (!typeEquals(types.toArray(new Class[types.size()]), handleParamTypes)) {
				types.add(Map.class);
				return typeEquals(types.toArray(new Class[types.size()]), handleParamTypes);
			}
			return true;
		case FINALLY:
			types.add(0, Exception.class);
			types.add(1, serviceMethod.getReturnType());
			if (!typeEquals(types.toArray(new Class[types.size()]), handleParamTypes)) {
				types.add(Map.class);
				return typeEquals(types.toArray(new Class[types.size()]), handleParamTypes);
			}
			return true;
		}
		return false;
	}

	private String cacheKey(Object obj, Phase phase) {
		if (obj instanceof Method) {
			return ((Method) obj).toGenericString() + "." + phase;
		}
		if (obj instanceof Class) {
			return ((Class<?>) obj).getName();
		}
		return obj.toString() + phase;
	}

}
