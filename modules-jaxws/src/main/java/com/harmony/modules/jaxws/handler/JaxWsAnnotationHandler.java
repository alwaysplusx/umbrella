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
package com.harmony.modules.jaxws.handler;

import static com.harmony.modules.jaxws.Phase.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.modules.core.BeanLoader;
import com.harmony.modules.core.ClassBeanLoader;
import com.harmony.modules.core.InvokException;
import com.harmony.modules.jaxws.Handler.HandleMethod;
import com.harmony.modules.jaxws.JaxWsAbortException;
import com.harmony.modules.jaxws.JaxWsContext;
import com.harmony.modules.jaxws.JaxWsContextHandler;
import com.harmony.modules.jaxws.util.HandleMethodInvoker;
import com.harmony.modules.jaxws.util.JaxWsHandlerMethodFinder;
import com.harmony.modules.utils.Exceptions;

/**
 * 将{@linkplain JaxWsAnnotationHandler#defaultPackage}下带有{@linkplain Handler}
 * annotation的类, 加载为交互各个时段的处理器
 * @author wuxii@foxmail.com
 * @see Handler
 * @see HandleMethod
 */
public class JaxWsAnnotationHandler implements JaxWsContextHandler {

	private static final long serialVersionUID = -2065293420059388476L;
	protected static final String defaultPackage = "com.harmony";

	private final static Logger log = LoggerFactory.getLogger(JaxWsAnnotationHandler.class);

	private BeanLoader beanLoader = new ClassBeanLoader();
	private JaxWsHandlerMethodFinder finder;

	public JaxWsAnnotationHandler() {
		this(defaultPackage);
	}

	public JaxWsAnnotationHandler(String basePackage) {
		this.finder = new JaxWsHandlerMethodFinder(basePackage);
	}

	@Override
	public boolean preExecute(JaxWsContext context) throws JaxWsAbortException {
		try {
			HandleMethodInvoker[] invokers = finder.findHandleMethods(context.getMethod(), PRE_INVOKE);
			for (HandleMethodInvoker invoker : invokers) {
				try {
					if (invoker.isEndWithMap()) {
						invoker.setContextMap(context.getContextMap());
					}
					Object bean = beanLoader.loadBean(invoker.getHandlerClass());
					Object result = invoker.invokeHandleMethod(bean, context.getParameters());
					if (result instanceof Boolean && !Boolean.valueOf((Boolean) result)) {
						return false;
					}
				} catch (InvokException e) {
					throw new JaxWsAbortException("执行handleMethodInvoker[" + invoker + "]方法失败", Exceptions.getRootCause(e));
				}
			}
		} catch (NoSuchMethodException e) {
			throw new JaxWsAbortException(context + "对应的方法不存在", e);
		}
		return true;
	}

	@Override
	public void abortExecute(JaxWsContext context, JaxWsAbortException exception) {
		try {
			HandleMethodInvoker[] invokers = finder.findHandleMethods(context.getMethod(), ABORT);
			for (HandleMethodInvoker invoker : invokers) {
				try {
					if (invoker.isEndWithMap()) {
						invoker.setContextMap(context.getContextMap());
					}
					invoker.setThrowable(exception);
					Object bean = beanLoader.loadBean(invoker.getHandlerClass());
					invoker.invokeHandleMethod(bean, context.getParameters());
				} catch (InvokException e) {
					log.error("执行handleMethodInvoker[{}]方法失败", invoker, e);
					return;
				}
			}
		} catch (NoSuchMethodException e) {
			log.error("{}方法不存在", context, e);
		}
	}

	@Override
	public void postExecute(JaxWsContext context, Object result) {
		try {
			HandleMethodInvoker[] invokers = finder.findHandleMethods(context.getMethod(), POST_INVOKE);
			for (HandleMethodInvoker invoker : invokers) {
				try {
					if (invoker.isEndWithMap()) {
						invoker.setContextMap(context.getContextMap());
					}
					invoker.setResult(result);
					Object bean = beanLoader.loadBean(invoker.getHandlerClass());
					invoker.invokeHandleMethod(bean, context.getParameters());
				} catch (InvokException e) {
					log.error("执行handleMethodInvoker[{}]方法失败", invoker, e);
					return;
				}
			}
		} catch (NoSuchMethodException e) {
			log.error("{}方法不存在", context, e);
		}
	}

	@Override
	public void throwing(JaxWsContext context, Throwable throwable) {
		try {
			HandleMethodInvoker[] invokers = finder.findHandleMethods(context.getMethod(), POST_INVOKE);
			for (HandleMethodInvoker invoker : invokers) {
				try {
					if (invoker.isEndWithMap()) {
						invoker.setContextMap(context.getContextMap());
					}
					invoker.setThrowable(throwable);
					Object bean = beanLoader.loadBean(invoker.getHandlerClass());
					invoker.invokeHandleMethod(bean, context.getParameters());
				} catch (InvokException e) {
					log.error("执行handleMethodInvoker[{}]方法失败", invoker, e);
					return;
				}
			}
		} catch (NoSuchMethodException e) {
			log.error("{}方法不存在", context, e);
		}
	}

	@Override
	public void finallyExecute(JaxWsContext context, Object result, Exception exception) {
		try {
			HandleMethodInvoker[] invokers = finder.findHandleMethods(context.getMethod(), POST_INVOKE);
			for (HandleMethodInvoker invoker : invokers) {
				try {
					if (invoker.isEndWithMap()) {
						invoker.setContextMap(context.getContextMap());
					}
					invoker.setResult(result);
					invoker.setThrowable(exception);
					Object bean = beanLoader.loadBean(invoker.getHandlerClass());
					invoker.invokeHandleMethod(bean, context.getParameters());
				} catch (InvokException e) {
					log.error("执行handleMethodInvoker[{}]方法失败", invoker, e);
					return;
				}
			}
		} catch (NoSuchMethodException e) {
			log.error("{}方法不存在", context, e);
		}
	}

	public void setBeanLoader(BeanLoader beanLoader) {
		this.beanLoader = beanLoader;
	}

}
