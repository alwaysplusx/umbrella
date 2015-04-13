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
package com.harmony.umbrella.monitor;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.monitor.support.MethodExpressionMatcherImpl;

/**
 * @author wuxii@foxmail.com
 */
public class AbstractMethodMonitor<T> implements MethodMonitor {

    protected static final Logger log = LoggerFactory.getLogger(AbstractMethodMonitor.class);

    protected final static Object DEFAULT_METHOD_VALUE = new Object();

    private Map<String, MethodExpressionMatcher> matcherList = new ConcurrentHashMap<String, MethodExpressionMatcher>();

    private MonitorPolicy policy = MonitorPolicy.WhiteList;

    private Map<Method, Object> methodList = new ConcurrentHashMap<Method, Object>();

    protected void persistGraph(MethodGraph graph) {
    }

    protected Method getMethod(T ctx) {
        return null;
    }

    protected Object getTarget(T ctx) {
        return null;
    }

    protected Object process(T ctx) throws Exception {
        return null;
    }

    protected Object[] getParameters(T ctx) {
        return null;
    }

    @Override
    public boolean isMonitored(Method method) {
        switch (policy) {
        case Skip:
            return false;
        case All:
            return true;
        case BlockList:
            for (MethodExpressionMatcher matcher : matcherList.values()) {
                if (matcher.matches(method) && methodList.containsKey(method)) {
                    return true;
                }
            }
            return false;
        case WhiteList:
            for (MethodExpressionMatcher matcher : matcherList.values()) {
                if (matcher.matches(method) && !methodList.containsKey(method)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    protected MethodExpressionMatcher createMatcher(String pattern) {
        return new MethodExpressionMatcherImpl(pattern);
    }

    protected Object monitor(T ctx) throws Exception {
        Method method = getMethod(ctx);
        if (isMonitored(method)) {
            Object result = null;
            Object target = getTarget(ctx);
            Object[] parameters = getParameters(ctx);
            log.debug("interceptor method [{}] of [{}]", method, target);
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
                    log.debug("", e);
                }
            }
        }
        return process(ctx);
    }

    @Override
    public MonitorPolicy getPolicy() {
        return policy;
    }

    @Override
    public void excludePattern(String pattern) {
        matcherList.remove(pattern);
    }

    @Override
    public void includePattern(String pattern) {
        matcherList.put(pattern, createMatcher(pattern));
    }

    @Override
    public void excludeMethod(Method method) {
        methodList.remove(method);
    }

    @Override
    public void includeMethod(Method method) {
        methodList.put(method, DEFAULT_METHOD_VALUE);
    }

    @Override
    public Set<String> getPatterns() {
        return Collections.unmodifiableSet(matcherList.keySet());
    }

}
