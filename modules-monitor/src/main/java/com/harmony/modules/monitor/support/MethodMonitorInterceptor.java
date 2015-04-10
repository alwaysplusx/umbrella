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
package com.harmony.modules.monitor.support;

import java.lang.reflect.Method;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import com.harmony.modules.monitor.MethodMonitor;
import com.harmony.modules.monitor.MethodMonitorAdapter;
import com.harmony.modules.monitor.annotation.Monitored;

/**
 * TODO 将监控方法抽象，带入泛型指定具体的Invocation
 * @author wuxii@foxmail.com
 */
@Monitored
@Interceptor
public class MethodMonitorInterceptor extends MethodMonitorAdapter<InvocationContext> implements MethodMonitor {

    @Override
    @AroundInvoke
    protected Object monitor(InvocationContext ctx) throws Exception {
        return super.monitor(ctx);
    }

    @Override
    protected void persistGraph(MethodGraph graph) {
    }

    @Override
    protected Method getMethod(InvocationContext ctx) {
        return ctx.getMethod();
    }

    @Override
    protected Object getTarget(InvocationContext ctx) {
        return ctx.getTarget();
    }

    @Override
    protected Object process(InvocationContext ctx) throws Exception {
        return ctx.proceed();
    }

    @Override
    protected Object[] getParameters(InvocationContext ctx) {
        return ctx.getParameters();
    }

}
