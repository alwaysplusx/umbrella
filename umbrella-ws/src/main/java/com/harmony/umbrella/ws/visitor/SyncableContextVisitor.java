/*
 * Copyright 2002-2015 the original author or authors.
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
package com.harmony.umbrella.ws.visitor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.harmony.umbrella.context.ApplicationContext;
import com.harmony.umbrella.core.BeanFactory;
import com.harmony.umbrella.ws.Context;
import com.harmony.umbrella.ws.ProxyExecutor;
import com.harmony.umbrella.ws.WebServiceAbortException;
import com.harmony.umbrella.ws.annotation.Syncable;
import com.harmony.umbrella.ws.proxy.Proxy;
import com.harmony.umbrella.ws.proxy.ProxyCallback;
import com.harmony.umbrella.ws.proxy.ProxyResultCallback;
import com.harmony.umbrella.ws.util.CallbackFinder;

/**
 * 业务回调的扩展周期访问者
 * <p>
 * 基于 {@linkplain Syncable}, {@linkplain ProxyCallback}的功能扩展，
 * {@linkplain SyncableContextVisitor}加载类路径下的标注有{@linkplain Syncable} 注解的类（注有
 * {@linkplain Syncable}表示一个可同步的业务bean）。
 * <p>
 * {@linkplain ProxyCallback}接口的实现类对注有{@linkplain Syncable} 的同步业务bean起到回调作用。将
 * {@linkplain SyncableContextVisitor}注入到{@linkplain ProxyExecutor}中则客户实现对
 * {@linkplain ProxyCallback}的实现在同步业务上的周期回调
 * 
 * @author wuxii@foxmail.com
 */
public class SyncableContextVisitor extends AbstractContextVisitor {
    
    private static final String DEFAULT_PACKAGE = ApplicationContext.APPLICATION_PACKAGE;
    
    /**
     * 用户扫描类路径下的{@linkplain Syncable}
     */
    private CallbackFinder callbackFinder;

    /**
     * 负责初始化回调的{@linkplain ProxyCallback}
     */
    private BeanFactory beanFactory;

    public SyncableContextVisitor() {
        this(DEFAULT_PACKAGE);
    }

    public SyncableContextVisitor(String basePackage) {
        this.callbackFinder = new CallbackFinder(basePackage);
    }

    public SyncableContextVisitor(String basePackage, BeanFactory beanFactory) {
        this.callbackFinder = new CallbackFinder(basePackage);
        this.beanFactory = beanFactory;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public boolean visitBefore(Context context) throws WebServiceAbortException {
        Object obj = context.get(Proxy.SYNC_OBJECT);
        Map<String, Object> content = context.getContextMap();

        for (ProxyCallback callback : getCallbacks(context)) {
            for (Object o : asList(obj)) {
                callback.forward(o, content);
            }
        }

        return true;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void visitCompletion(Object result, Context context) {
        Object obj = context.get(Proxy.SYNC_OBJECT);
        Map<String, Object> content = context.getContextMap();

        try {
            Method method = context.getMethod();
            Class<?> returnType = method.getReturnType();

            // 返回值是否经过封装标识(default false)
            // 有值返回，返回数据的值类型与方法返回类型不相同表示结果经过封装
            boolean wrapped = result != null && result.getClass().isAssignableFrom(returnType);

            for (ProxyCallback callback : getCallbacks(context)) {
                for (Object o : asList(obj)) {
                    if (callback instanceof ProxyResultCallback) {
                        // 支持ProxySyncResult作为结果参数
                        callback.success(o, ((ProxyResultCallback) callback).newResult(result, wrapped), content);
                    } else {
                        callback.success(o, result, content);
                    }
                }
            }
        } catch (NoSuchMethodException e) {
            // impossible
        }

    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void visitThrowing(Throwable throwable, Context context) {
        Object obj = context.get(Proxy.SYNC_OBJECT);
        Map<String, Object> content = context.getContextMap();
        for (ProxyCallback callback : getCallbacks(context)) {
            for (Object o : asList(obj)) {
                callback.failed(o, throwable, content);
            }
        }
    }

    public void setCallbackFinder(CallbackFinder callbackFinder) {
        this.callbackFinder = callbackFinder;
    }

    /*
     * 延迟加载
     */
    protected BeanFactory getBeanFactory() {
        if (beanFactory == null) {
            beanFactory = ApplicationContext.getApplicationContext();
        }
        return beanFactory;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private List asList(Object obj) {
        return obj instanceof Collection ? new ArrayList((Collection) obj) : Arrays.asList(obj);
    }

    @SuppressWarnings("rawtypes")
    private List<ProxyCallback> getCallbacks(Context context) {
        List<ProxyCallback> result = new ArrayList<ProxyCallback>();
        Class<ProxyCallback>[] classes = callbackFinder.getCallbackClasses(context.getServiceClass(), context.getMethodName());
        if (classes != null && classes.length > 0) {
            BeanFactory beanFactory = getBeanFactory();
            for (Class<ProxyCallback> callbackClass : classes) {
                result.add(beanFactory.getBean(callbackClass));
            }
        }
        return result;
    }

}
