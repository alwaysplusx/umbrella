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
import com.opensymphony.xwork2.interceptor.Interceptor;

/**
 * @author wuxii@foxmail.com
 */
public class StrutsInterceptorMethodMonitor extends MethodMonitorInterceptor<ActionInvocation> implements Interceptor {

    private static final long serialVersionUID = -6402962474792262484L;

    @Override
    public void init() {
    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        return null;
    }

    @Override
    public void destroy() {
    }

    @Override
    protected Method getMethod(ActionInvocation ctx) {
        return null;
    }

    @Override
    protected Object getTarget(ActionInvocation ctx) {
        return null;
    }

    @Override
    protected Object process(ActionInvocation ctx) throws Exception {
        return ctx.invoke();
    }

    @Override
    protected Object[] getParameters(ActionInvocation ctx) {
        return null;
    }

}
