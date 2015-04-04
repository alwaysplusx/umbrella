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
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SimpleJaxWsContext implements JaxWsContext, Serializable {

	private static final long serialVersionUID = -7702092080209756058L;
	private Map<String, String> headers = new HashMap<String, String>();
	private Class<?> serviceInterface;
	private String methodName;
	private Object[] parameters;
	private String address;
	private String username;
	private String password;

	public SimpleJaxWsContext() {
	}

	public SimpleJaxWsContext(Class<?> serviceInterface, String methodName, Object[] parameters) {
		this.serviceInterface = serviceInterface;
		this.methodName = methodName;
		this.parameters = parameters;
	}

	@Override
	public Class<?> getServiceInterface() {
		return this.serviceInterface;
	}

	@Override
	public Object[] getParameters() {
		return this.parameters;
	}

	@Override
	public String getMethodName() {
		return this.methodName;
	}

	public void setServiceInterface(Class<?> serviceInterface) {
		this.serviceInterface = serviceInterface;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}

	public void put(String name, String value) {
		headers.put(name, value);
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String get(String name) {
		return headers.get(name);
	}

	@Override
	public boolean contains(String name) {
		return headers.containsKey(name);
	}

	@Override
	public Method getMethod() throws NoSuchMethodException {
		try {
			return serviceInterface.getMethod(methodName, toParameterTypes(parameters));
		} catch (NoSuchMethodException e) {
			for (Method method : serviceInterface.getMethods()) {
				if (method.getName().equals(methodName)) {
					Class<?>[] types = method.getParameterTypes();
					if (types.length != parameters.length)
						continue;
					int i = 0;
					for (; i < types.length; i++) {
						if (parameters[i] != null && !types[i].getName().equals(parameters[i].getClass().getName())) {
							break;
						}
					}
					if (i == types.length) {
						return method;
					}
				}
			}
			throw e;
		}
	}

	@Override
	public Map<String, String> getContextMap() {
		HashMap<String, String> contextMap = new HashMap<String, String>();
		contextMap.putAll(headers);
		return contextMap;
	}

	protected Class<?>[] toParameterTypes(Object[] args) {
		Class<?>[] parameterTypes = new Class<?>[0];
		if (args != null) {
			parameterTypes = new Class[args.length];
			for (int i = 0; i < args.length; i++) {
				if (args[i] != null)
					parameterTypes[i] = args[i].getClass();
				else
					parameterTypes[i] = Object.class;
			}
		}
		return parameterTypes;
	}

	@Override
	public Enumeration<String> getContextHeaderNames() {
		return new Enumeration<String>() {
			Iterator<String> it = headers.keySet().iterator();

			@Override
			public boolean hasMoreElements() {
				return it.hasNext();
			}

			@Override
			public String nextElement() {
				return it.next();
			}

		};
	}

	@Override
	public String toString() {
		String args = Arrays.toString(parameters).replace("[", "").replace("]", "");
		return "{" + serviceInterface.getName() + "#" + methodName + "(" + args + ")}";
	}

}
