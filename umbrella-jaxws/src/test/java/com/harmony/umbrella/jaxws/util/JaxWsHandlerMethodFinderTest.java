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
package com.harmony.umbrella.jaxws.util;

import static com.harmony.umbrella.jaxws.Phase.*;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.harmony.umbrella.jaxws.Handler;
import com.harmony.umbrella.jaxws.HelloService;
import com.harmony.umbrella.jaxws.Phase;
import com.harmony.umbrella.jaxws.util.HandleMethodInvoker;
import com.harmony.umbrella.jaxws.util.JaxWsHandlerMethodFinder;

/**
 * @author wuxii@foxmail.com
 */
public class JaxWsHandlerMethodFinderTest {

	private Phase[] phases = new Phase[] { PRE_INVOKE, ABORT, POST_INVOKE, THROWING, FINALLY };
	JaxWsHandlerMethodFinder finder = new JaxWsHandlerMethodFinder("com.harmony");

	@Test
	public void test() throws Exception {
		Class<?>[] handlerClass = finder.findHandlerClass(HelloService.class);
		Method serviceMethod = HelloService.class.getMethod("sayHi", String.class);
		for (Phase phase : phases) {
			HandleMethodInvoker[] handleMethods = finder.findHandleMethods(serviceMethod, handlerClass[0], phase);
			for (HandleMethodInvoker hmi : handleMethods) {
				System.out.println(hmi);
			}
		}
	}

	@Test
	public void testResolve() {
		Map<String, HandleMethodInvoker[]> result = resolve();
		Collection<HandleMethodInvoker[]> values = result.values();
		for (HandleMethodInvoker[] hmis : values) {
			for (HandleMethodInvoker hmi : hmis) {
				System.out.println(hmi);
			}
		}
	}

	public Map<String, HandleMethodInvoker[]> resolve() {
		Map<String, HandleMethodInvoker[]> map = new HashMap<String, HandleMethodInvoker[]>();
		Set<Method> methods = allServiceMethod();
		for (Method method : methods) {
			for (Phase phase : phases) {
				HandleMethodInvoker[] hmis = finder.findHandleMethods(method, phase);
				map.put(method.getDeclaringClass().getName() + "#" + method.getName() + "." + phase, hmis);
			}
		}
		return map;
	}

	public Set<Method> allServiceMethod() {
		Set<Method> serviceMethod = new LinkedHashSet<Method>();
		for (Class<?> c : allServiceClass()) {
			Method[] methods = c.getMethods();
			for (Method method : methods) {
				if (method.getDeclaringClass() == Object.class)
					continue;
				serviceMethod.add(method);
			}
		}
		return serviceMethod;
	}

	public Set<Class<?>> allServiceClass() {
		Class<?>[] handlerClass = finder.getAllHandlerClass();
		Set<Class<?>> serviceClass = new LinkedHashSet<Class<?>>();
		for (Class<?> hc : handlerClass) {
			Handler handler = hc.getAnnotation(Handler.class);
			Collections.addAll(serviceClass, handler.value());
		}
		return serviceClass;
	}

}
