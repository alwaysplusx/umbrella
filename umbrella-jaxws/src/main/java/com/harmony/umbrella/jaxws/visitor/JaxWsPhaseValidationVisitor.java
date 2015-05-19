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
package com.harmony.umbrella.jaxws.visitor;

import static com.harmony.umbrella.jaxws.Phase.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.Constant;
import com.harmony.umbrella.core.BeanFactory;
import com.harmony.umbrella.core.InvokeException;
import com.harmony.umbrella.core.SimpleBeanFactory;
import com.harmony.umbrella.jaxws.JaxWsAbortException;
import com.harmony.umbrella.jaxws.JaxWsContext;
import com.harmony.umbrella.jaxws.JaxWsGraph;
import com.harmony.umbrella.jaxws.util.HandleMethodInvoker;
import com.harmony.umbrella.jaxws.util.JaxWsHandlerMethodFinder;
import com.harmony.umbrella.util.Exceptions;

/**
 * @author wuxii@foxmail.com
 */
public class JaxWsPhaseValidationVisitor extends AbstractJaxWsPhaseVisitor {

    private static final long serialVersionUID = 3905871275755920058L;

    private final static Logger log = LoggerFactory.getLogger(JaxWsPhaseValidationVisitor.class);

    protected static final String defaultPackage = Constant.DEFAULT_PACKAGE;

    private BeanFactory beanFactory = new SimpleBeanFactory();

    private JaxWsHandlerMethodFinder finder;

    private String scanPackage;

    public JaxWsPhaseValidationVisitor() {
        this(defaultPackage);
    }

    public JaxWsPhaseValidationVisitor(String scanPackage) {
        this.scanPackage = scanPackage;
        this.finder = new JaxWsHandlerMethodFinder(scanPackage);
    }

    @Override
    public boolean visitBefore(JaxWsContext context) throws JaxWsAbortException {
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
    public void visitAbort(JaxWsAbortException ex, JaxWsContext context) {
        try {
            HandleMethodInvoker[] invokers = finder.findHandleMethods(context.getMethod(), ABORT);
            for (HandleMethodInvoker invoker : invokers) {
                try {
                    if (invoker.isEndWithMap()) {
                        invoker.setContextMap(context.getContextMap());
                    }
                    invoker.setThrowable(ex);
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
    public void visitCompletion(Object result, JaxWsContext context) {
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
    public void visitThrowing(Throwable throwable, JaxWsContext context) {
        try {
            HandleMethodInvoker[] invokers = finder.findHandleMethods(context.getMethod(), THROWING);
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
    public void visitFinally(Object result, Throwable throwable, JaxWsGraph graph, JaxWsContext context) {
        try {
            HandleMethodInvoker[] invokers = finder.findHandleMethods(context.getMethod(), FINALLY);
            for (HandleMethodInvoker invoker : invokers) {
                try {
                    if (invoker.isEndWithMap()) {
                        invoker.setContextMap(context.getContextMap());
                    }
                    invoker.setResult(result);
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

    /**
     * 修改扫描包
     * 
     * @param scanPackage
     */
    public void changeScanPackage(String scanPackage) {
        log.info("修改JaxWSHandlerFinder的扫描路径[{} -> {}]", this.scanPackage, scanPackage);
        this.scanPackage = scanPackage;
        this.finder = new JaxWsHandlerMethodFinder(scanPackage);
    }

    public String getScanPackage() {
        return scanPackage;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
}
