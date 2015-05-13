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
package com.harmony.umbrella.jaxws.impl;

import static com.harmony.umbrella.jaxws.JaxWsProxyBuilder.*;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.core.InvokeException;
import com.harmony.umbrella.core.Invoker;
import com.harmony.umbrella.jaxws.JaxWsContext;
import com.harmony.umbrella.jaxws.JaxWsException;
import com.harmony.umbrella.jaxws.JaxWsGraph;
import com.harmony.umbrella.jaxws.JaxWsPhaseExecutor;
import com.harmony.umbrella.jaxws.util.JaxWsInvoker;
import com.harmony.umbrella.util.Exceptions;

/**
 * jaxWs CXF执行方式实现
 * 
 * @author wuxii@foxmail.com
 */
public class JaxWsCXFExecutor extends JaxWsPhaseExecutor {

    private static final Logger log = LoggerFactory.getLogger(JaxWsCXFExecutor.class);
    private Map<JaxWsContextKey, Object> proxyCache = new HashMap<JaxWsContextKey, Object>();
    private boolean cacheable = true;
    private Invoker invoker = new JaxWsInvoker();

    @SuppressWarnings("unchecked")
    @Override
    public <T> T executeQuite(JaxWsContext context, Class<T> resultType) {
        T result = null;
        JaxWsGraphImpl graph = new JaxWsGraphImpl();
        context.put(JaxWsGraph.JAXWS_CONTEXT_GRAPH, graph);
        try {
            Method method = context.getMethod();
            Object proxy = loadProxy(context);
            Object[] parameters = context.getParameters();
            graph.setMethod(method);
            graph.setTarget(proxy);
            graph.setArgs(parameters);
            log.info("使用代理[{}]执行交互{}, invoker is [{}]", proxy, context, invoker);
            result = (T) invoker.invoke(proxy, method, parameters);
            graph.setResult(result);
        } catch (NoSuchMethodException e) {
            graph.setException(e);
            throw new JaxWsException("未找到接口方法" + context, e);
        } catch (InvokeException e) {
            graph.setException(e);
            throw new JaxWsException("执行交互失败", Exceptions.getRootCause(e));
        } finally {
            graph.setResponseTime(Calendar.getInstance());
        }
        return result;
    }

    @Override
    protected void persistGraph(JaxWsGraph graph) {
        // empty implement
    }

    /**
     * 重缓存中获取执行上下文对应的代理服务
     * 
     * @param context
     * @return
     */
    protected Object getProxy(JaxWsContext context) {
        JaxWsContextKey contextKey = new JaxWsContextKey(context);
        if (!proxyCache.containsKey(contextKey)) {
            Object proxy = createProxy(context);
            proxyCache.put(contextKey, proxy);
        }
        return proxyCache.get(contextKey);
    }

    public boolean isCacheable() {
        return cacheable;
    }

    /**
     * 设置是否从缓存中获取服务
     * 
     * @param cacheable
     */
    public void setCacheable(boolean cacheable) {
        this.cacheable = cacheable;
    }

    /**
     * 设置执行交互的{@linkplain Invoker}
     * <p>
     * 默认是{@linkplain JaxWsInvoker}
     * 
     * @param invoker
     */
    public void setInvoker(Invoker invoker) {
        this.invoker = invoker;
    }

    /**
     * 创建当前{@linkplain JaxWsContext}对应的服务代理
     * 
     * @param context
     *            执行上下文
     * @return
     */
    protected Object createProxy(JaxWsContext context) {
        log.info("创建{}的代理服务", context);
        return newProxyBuilder().setAddress(context.getAddress()).setUsername(context.getUsername()).setPassword(context.getPassword())
                .build(context.getServiceInterface());
    }

    /**
     * 清除已经缓存的服务代理
     */
    public void cleanPool() {
        log.info("清除已经缓冲的代理对象");
        proxyCache.clear();
    }

    private Object loadProxy(JaxWsContext context) {
        Object proxy = null;
        if (cacheable) {
            proxy = getProxy(context);
        } else {
            proxy = createProxy(context);
        }
        return proxy;
    }

    private static class JaxWsContextKey {

        private String serviceName;
        private String address;
        private String username;
        private String password;

        public JaxWsContextKey(JaxWsContext context) {
            this.serviceName = context.getServiceInterface().getName();
            this.address = context.getAddress();
            this.username = context.getUsername();
            this.password = context.getPassword();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((address == null) ? 0 : address.hashCode());
            result = prime * result + ((password == null) ? 0 : password.hashCode());
            result = prime * result + ((serviceName == null) ? 0 : serviceName.hashCode());
            result = prime * result + ((username == null) ? 0 : username.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            JaxWsContextKey other = (JaxWsContextKey) obj;
            if (address == null) {
                if (other.address != null)
                    return false;
            } else if (!address.equals(other.address))
                return false;
            if (password == null) {
                if (other.password != null)
                    return false;
            } else if (!password.equals(other.password))
                return false;
            if (serviceName == null) {
                if (other.serviceName != null)
                    return false;
            } else if (!serviceName.equals(other.serviceName))
                return false;
            if (username == null) {
                if (other.username != null)
                    return false;
            } else if (!username.equals(other.username))
                return false;
            return true;
        }
    }

}
