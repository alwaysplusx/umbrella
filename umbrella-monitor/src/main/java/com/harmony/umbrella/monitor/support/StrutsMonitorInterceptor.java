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
package com.harmony.umbrella.monitor.support;

import java.lang.reflect.Method;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import com.opensymphony.xwork2.interceptor.Interceptor;

/**
 * @author wuxii@foxmail.com
 */
public class StrutsMonitorInterceptor extends MethodMonitorInterceptor<ActionInvocation> implements Interceptor {

    private static final long serialVersionUID = -6402962474792262484L;

    @Override
    public void init() {
    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        if (getMethod(invocation) != null) {
            return (String) monitor(invocation);
        }
        return invocation.invoke();
    }

    @Override
    protected Method getMethod(ActionInvocation ctx) {
        ActionProxy proxy = ctx.getProxy();
        String methodName = proxy.getMethod();
        if (methodName == null) {
            methodName = proxy.getConfig().getMethodName();
        }
        try {
            return ctx.getAction().getClass().getMethod(methodName);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    @Override
    protected Object getTarget(ActionInvocation ctx) {
        return ctx.getAction();
    }

    @Override
    protected Object process(ActionInvocation ctx) throws Exception {
        return ctx.invoke();
    }

    @Override
    protected Object[] getParameters(ActionInvocation ctx) {
        return new Object[] {};
    }

    @Override
    public void destroy() {
    }

}
