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
package com.harmony.umbrella.jaxws.handler;

import static com.harmony.umbrella.jaxws.Phase.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.core.BeanFactory;
import com.harmony.umbrella.core.SimpleBeanFactory;
import com.harmony.umbrella.core.InvokeException;
import com.harmony.umbrella.jaxws.JaxWsAbortException;
import com.harmony.umbrella.jaxws.JaxWsContext;
import com.harmony.umbrella.jaxws.JaxWsContextHandler;
import com.harmony.umbrella.jaxws.Handler.HandleMethod;
import com.harmony.umbrella.jaxws.util.HandleMethodInvoker;
import com.harmony.umbrella.jaxws.util.JaxWsHandlerMethodFinder;
import com.harmony.umbrella.util.Exceptions;

/**
 * 将{@linkplain JaxWsAnnotationHandler#scanPackage scanPackage}下带有{@linkplain com.harmony.umbrella.jaxws.Handler Handler}
 * annotation的类, 加载为交互各个时段的处理器
 * @author wuxii@foxmail.com
 * @see Handler
 * @see HandleMethod
 */
public class JaxWsAnnotationHandler implements JaxWsContextHandler {

    private static final long serialVersionUID = -2065293420059388476L;
    protected static final String defaultPackage = "com.harmony";

    private final static Logger log = LoggerFactory.getLogger(JaxWsAnnotationHandler.class);

    private BeanFactory beanFactory = new SimpleBeanFactory();
    private JaxWsHandlerMethodFinder finder;
    private String scanPackage = defaultPackage;

    public JaxWsAnnotationHandler() {
        this(defaultPackage);
    }

    public JaxWsAnnotationHandler(String scanPackage) {
        this.scanPackage = scanPackage;
        this.finder = new JaxWsHandlerMethodFinder(scanPackage);
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
                    Object bean = beanFactory.getBean(invoker.getHandlerClass());
                    Object result = invoker.invokeHandleMethod(bean, context.getParameters());
                    if (result instanceof Boolean && !Boolean.valueOf((Boolean) result)) {
                        return false;
                    }
                } catch (InvokeException e) {
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
                    Object bean = beanFactory.getBean(invoker.getHandlerClass());
                    invoker.invokeHandleMethod(bean, context.getParameters());
                } catch (InvokeException e) {
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
                    Object bean = beanFactory.getBean(invoker.getHandlerClass());
                    invoker.invokeHandleMethod(bean, context.getParameters());
                } catch (InvokeException e) {
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
                    Object bean = beanFactory.getBean(invoker.getHandlerClass());
                    invoker.invokeHandleMethod(bean, context.getParameters());
                } catch (InvokeException e) {
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
                    Object bean = beanFactory.getBean(invoker.getHandlerClass());
                    invoker.invokeHandleMethod(bean, context.getParameters());
                } catch (InvokeException e) {
                    log.error("执行handleMethodInvoker[{}]方法失败", invoker, e);
                    return;
                }
            }
        } catch (NoSuchMethodException e) {
            log.error("{}方法不存在", context, e);
        }
    }

    public void changeScanPackage(String scanPackage) {
        this.scanPackage = scanPackage;
        this.finder = new JaxWsHandlerMethodFinder(scanPackage);
    }

    public String getScanPackage() {
        return scanPackage;
    }

    public void setBeanLoader(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

}
