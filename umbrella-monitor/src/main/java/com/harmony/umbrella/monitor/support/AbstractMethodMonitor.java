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
import java.util.Calendar;

import com.harmony.umbrella.monitor.AbstractMonitor;
import com.harmony.umbrella.monitor.DefaultMethodGraph;
import com.harmony.umbrella.monitor.MethodMonitor;
import com.harmony.umbrella.monitor.ResourceMatcher;

/**
 * @author wuxii@foxmail.com
 */
public abstract class AbstractMethodMonitor<I> extends AbstractMonitor<Method> implements MethodMonitor {

    protected abstract void persistGraph(MethodGraph graph);

    protected abstract Method getMethod(I ctx);

    protected abstract Object getTarget(I ctx);

    protected abstract Object process(I ctx) throws Exception;

    protected abstract Object[] getParameters(I ctx);

    protected Object monitor(I ctx) throws Exception {
        Method method = getMethod(ctx);
        if (isMonitored(method)) {
            Object result = null;
            Object target = getTarget(ctx);
            Object[] parameters = getParameters(ctx);
            LOG.debug("interceptor method [{}] of [{}]", method, target);
            DefaultMethodGraph graph = new DefaultMethodGraph(target, method, parameters);
            try {
                result = process(ctx);
                graph.setResult(result);
            } catch (Exception e) {
                graph.setException(e);
                throw e;
            } finally {
                graph.setResponseTime(Calendar.getInstance());
                try {
                    persistGraph(graph);
                } catch (Exception e) {
                    LOG.debug("", e);
                }
            }
        }
        return process(ctx);
    }

    @Override
    protected ResourceMatcher<Method> createMatcher(String pattern) {
        return new MethodExpressionMatcher(pattern);
    }

}
