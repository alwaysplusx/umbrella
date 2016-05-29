package com.harmony.umbrella.ws.visitor;

import static com.harmony.umbrella.ws.Phase.*;

import java.io.Serializable;

import com.harmony.umbrella.core.BeanFactory;
import com.harmony.umbrella.core.InvokeException;
import com.harmony.umbrella.core.SimpleBeanFactory;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.util.Exceptions;
import com.harmony.umbrella.ws.Context;
import com.harmony.umbrella.ws.WebServiceAbortException;
import com.harmony.umbrella.ws.util.HandleMethodInvoker;
import com.harmony.umbrella.ws.util.HandlerMethodFinder;

/**
 * 通过{@linkplain com.harmony.umbrella.ws.annotation.Handler Handler}与
 * {@linkplain com.harmony.umbrella.ws.annotation.Handler.HandleMethod HandleMethod}
 * 来对同步业务进行周期回调
 * 
 * @author wuxii@foxmail.com
 */
public class ValidationContextVisitor extends AbstractContextVisitor implements Serializable {

    private static final long serialVersionUID = 3905871275755920058L;

    private static final Log log = Logs.getLog(ValidationContextVisitor.class);

    private final HandlerMethodFinder finder;

    private BeanFactory beanFactory;

    public ValidationContextVisitor(String scanPackage) {
        this(scanPackage, new SimpleBeanFactory());
    }

    public ValidationContextVisitor(String scanPackage, BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        this.finder = new HandlerMethodFinder(scanPackage);
    }

    @Override
    public boolean visitBefore(Context context) throws WebServiceAbortException {
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
                    throw new WebServiceAbortException("执行handleMethodInvoker[" + invoker + "]方法失败", Exceptions.getRootCause(e));
                }
            }
        } catch (NoSuchMethodException e) {
            throw new WebServiceAbortException(context + "对应的方法不存在", e);
        }
        return true;
    }

    @Override
    public void visitAbort(WebServiceAbortException ex, Context context) {
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
    public void visitCompletion(Object result, Context context) {
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
    public void visitThrowing(Throwable throwable, Context context) {
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
    public void visitFinally(Object result, Throwable throwable, Context context) {
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

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
}
