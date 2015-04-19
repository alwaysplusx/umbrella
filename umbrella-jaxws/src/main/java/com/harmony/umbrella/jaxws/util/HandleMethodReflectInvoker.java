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

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.harmony.umbrella.core.DefaultInvoker;
import com.harmony.umbrella.core.InvokeException;
import com.harmony.umbrella.jaxws.Phase;

/**
 * @author wuxii@foxmail.com
 */
public class HandleMethodReflectInvoker extends DefaultInvoker implements HandleMethodInvoker {

	private static final long serialVersionUID = 1L;

	private Class<?> handlerClass;
	private Method handleMethod;
	private boolean endWithMap;
	private Phase phase;
	private Throwable throwable;
	private Object result;
	private Map<String, Object> contextMap;

	public HandleMethodReflectInvoker() {
		super();
	}

	public HandleMethodReflectInvoker(Class<?> handlerClass, Method handleMethod, Phase phase) {
		super();
		this.handlerClass = handlerClass;
		this.handleMethod = handleMethod;
		this.phase = phase;
	}

	public HandleMethodReflectInvoker(Class<?> handlerClass, Method handleMethod, boolean endWithMap, Phase phase) {
		super();
		this.handlerClass = handlerClass;
		this.handleMethod = handleMethod;
		this.endWithMap = endWithMap;
		this.phase = phase;
	}

	@Override
	public Object invokeHandleMethod(Object target, Object[] args) throws InvokeException {
		return invokeHandleMethod(target, args, null);
	}

	@Override
	public Object invokeHandleMethod(Object target, Object[] args, Map<String, String> contextMap) throws InvokeException {
		List<Object> arguments = new LinkedList<Object>();
		Collections.addAll(arguments, args);
		if (endWithMap) {
			arguments.add(contextMap);
		}
		Phase phase = getPhase();
		switch (phase) {
		case PRE_INVOKE:
			break;
		case ABORT:
			arguments.add(0, getThrowable());
			break;
		case POST_INVOKE:
			arguments.add(0, getResult());
			break;
		case THROWING:
			arguments.add(0, getThrowable());
			break;
		case FINALLY:
			arguments.add(0, getResult());
			arguments.add(0, getThrowable());
			break;
		}
		return invoke(target, getHandleMethod(), arguments.toArray(new Object[arguments.size()]));
	}

	@Override
	public Class<?> getHandlerClass() {
		return handlerClass;
	}

	@Override
	public Method getHandleMethod() {
		return handleMethod;
	}

	@Override
	public Phase getPhase() {
		return phase;
	}

	@Override
	public boolean isEndWithMap() {
		return endWithMap;
	}

	public void setEndWithMap(boolean endWithMap) {
		this.endWithMap = endWithMap;
	}

	public void setHandlerClass(Class<?> handlerClass) {
		this.handlerClass = handlerClass;
	}

	public void setHandleMethod(Method handleMethod) {
		this.handleMethod = handleMethod;
	}

	public void setPhase(Phase phase) {
		this.phase = phase;
	}

	public Throwable getThrowable() {
		return throwable;
	}

	@Override
	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}

	public Object getResult() {
		return result;
	}

	@Override
	public void setResult(Object result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return handleMethod + "." + phase;
	}

	@Override
	public void setContextMap(Map<String, Object> contextMap) {
		this.contextMap = contextMap;
	}

	public Map<String, Object> getContextMap() {
		return contextMap;
	}

}
