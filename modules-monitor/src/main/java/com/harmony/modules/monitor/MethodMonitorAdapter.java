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
package com.harmony.modules.monitor;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.modules.monitor.support.MethodMonitorInterceptor;
import com.harmony.modules.monitor.util.MonitorUtils;

/**
 * @author wuxii@foxmail.com
 */
public abstract class MethodMonitorAdapter<T> implements MethodMonitor {

    protected static final Logger log = LoggerFactory.getLogger(MethodMonitorInterceptor.class);
    /**
     * 受到监视的资源
     */
    private Map<String, Object> monitorList = new HashMap<String, Object>();
    /**
     * 是否开启白名单策略，开启后只拦截在监视名单中的资源
     */
    private boolean useWhiteList;

    /**
     * 保存方法监视结果
     * @param graph
     */
    protected abstract void persistGraph(MethodGraph graph);

    protected abstract Method getMethod(T ctx);

    protected abstract Object getTarget(T ctx);

    protected abstract Object process(T ctx) throws Exception;

    protected abstract Object[] getParameters(T ctx);

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

    /**
     * 将方转化为唯一的资源限定表示
     * @param method
     * @return
     */
    protected String methodIdentifie(Method method) {
        return MonitorUtils.methodIdentifie(method);
    }

    @Override
    public void exclude(String resource) {
        monitorList.remove(resource);
    }

    @Override
    public void include(String resource) {
        monitorList.put(resource, null);
    }

    @Override
    public String[] getMonitorList() {
        Set<String> set = monitorList.keySet();
        return set.toArray(new String[set.size()]);
    }

    @Override
    public void exclude(Method method) {
        exclude(methodIdentifie(method));
    }

    @Override
    public void include(Method method) {
        include(methodIdentifie(method));
    }

    @Override
    public boolean isUseWhiteList() {
        return useWhiteList;
    }

    @Override
    public void useWhiteList(boolean use) {
        this.useWhiteList = use;
    }

    public void setWhiteList(boolean whiteList) {
        this.useWhiteList = whiteList;
    }

    @Override
    public boolean isMonitored(String resource) {
        if (useWhiteList) {
            return monitorList.containsKey(resource);
        }
        return true;
    }

    @Override
    public boolean isMonitored(Method method) {
        return isMonitored(methodIdentifie(method));
    }

}
