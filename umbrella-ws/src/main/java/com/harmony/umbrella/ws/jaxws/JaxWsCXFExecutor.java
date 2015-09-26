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
package com.harmony.umbrella.ws.jaxws;

import static com.harmony.umbrella.ws.jaxws.JaxWsProxyBuilder.*;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.ws.WebServiceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.core.InvokeException;
import com.harmony.umbrella.core.Invoker;
import com.harmony.umbrella.monitor.graph.DefaultMethodGraph;
import com.harmony.umbrella.util.Exceptions;
import com.harmony.umbrella.ws.Context;
import com.harmony.umbrella.ws.ProxyExecutorSupport;
import com.harmony.umbrella.ws.util.ContextValidatorUtils;
import com.harmony.umbrella.ws.util.JaxWsInvoker;

/**
 * JaxWs CXF执行方式实现
 * 
 * @author wuxii@foxmail.com
 */
public class JaxWsCXFExecutor extends ProxyExecutorSupport {

    private static final Logger log = LoggerFactory.getLogger(JaxWsCXFExecutor.class);

    /**
     * 代理对象缓存池
     */
    private Map<JaxWsContextKey, Object> proxyCache = new HashMap<JaxWsContextKey, Object>();

    /**
     * 配置是否接受缓存的代理对象
     */
    private boolean cacheable = true;

    private Invoker invoker = new JaxWsInvoker();

    @SuppressWarnings("unchecked")
    @Override
    public <T> T executeQuite(Context context, Class<T> resultType) {
        DefaultMethodGraph graph = null;
        T result = null;
        try {
            Method method = context.getMethod();
            graph = new DefaultMethodGraph(method);

            Object proxy = loadProxy(context);
            log.info("使用代理[{}]执行交互{}, invoker is [{}]", proxy, context, invoker);
            graph.setTarget(proxy);
            graph.setMethodArgumets(context.getParameters());

            graph.setRequestTime(Calendar.getInstance());
            result = (T) invoker.invoke(proxy, method, context.getParameters());
            graph.setResponseTime(Calendar.getInstance());

            graph.setMethodResult(result);
        } catch (NoSuchMethodException e) {
            throw new WebServiceException("未找到接口方法" + context, e);
        } catch (InvokeException e) {
            graph.setException(e);
            this.removeProxy(context.getServiceInterface());
            throw new WebServiceException("执行交互失败", Exceptions.getRootCause(e));
        } finally {
            if (graph != null) {
                LOG.debug("执行情况概要如下:{}", graph);
                context.put(WS_CONTEXT_GRAPH, graph);
            }
        }
        return result;
    }

    /**
     * 加载代理对象， 如果{@linkplain #cacheable}
     * 允许从缓存中加载泽加载缓存中的代理对象，如果缓存中不存在则新建一个代理对象，并将代理对象放置在缓存中
     * 
     * @param context
     *            执行的上下文
     * @return 代理对象
     */
    private Object loadProxy(Context context) {
        Object proxy = null;
        if (cacheable) {
            proxy = getProxy(context);
        } else {
            proxy = createProxy(context);
        }
        return configurationProxy(proxy, context);
    }

    /**
     * 缓存中获取执行上下文对应的代理服务
     * 
     * @param context
     *            执行的上下文
     * @return 代理对象， 如果不存在缓存中不存在则创建
     */
    protected Object getProxy(Context context) {
        ContextValidatorUtils.validation(context);

        // 代理的超时设置是次要因素，不考虑在代理对象的key中
        JaxWsContextKey contextKey = new JaxWsContextKey(context);
        Object proxy = proxyCache.get(contextKey);
        if (proxy != null) {
            return proxy;
        }

        Class<?> serviceInterface = context.getServiceInterface();

        synchronized (proxyCache) {
            Iterator<JaxWsContextKey> it = proxyCache.keySet().iterator();
            while (it.hasNext()) {
                JaxWsContextKey key = it.next();
                // 一个serviceInterface只缓存一个
                if (key.serviceName.equals(serviceInterface.getName())) {
                    it.remove();
                    break;
                }
            }
            proxy = createProxy(context);
            proxyCache.put(contextKey, proxy);
        }

        return proxy;
    }

    /**
     * 创建当前{@linkplain Context}对应的服务代理
     * 
     * @param context
     *            执行上下文
     * @return 代理对象
     */
    protected Object createProxy(Context context) {
        long start = System.currentTimeMillis();
        Object proxy = create()//
                .setAddress(context.getAddress())//
                .setUsername(context.getUsername())//
                .setPassword(context.getPassword())//
                .build(context.getServiceInterface());
        log.debug("创建代理{}服务, 耗时{}ms", context, System.currentTimeMillis() - start);
        return proxy;
    }

    /**
     * 移除缓存的代理对象
     */
    public void removeProxy(Class<?> serviceInterface) {
        if (serviceInterface == null) {
            return;
        }
        synchronized (proxyCache) {
            Iterator<JaxWsContextKey> it = proxyCache.keySet().iterator();
            while (it.hasNext()) {
                JaxWsContextKey key = it.next();
                if (serviceInterface.getName().equals(key.serviceName)) {
                    it.remove();
                }
            }
        }
    }

    /**
     * 清除已经缓存的服务代理
     */
    public void cleanPool() {
        synchronized (proxyCache) {
            proxyCache.clear();
        }
        log.info("清除已经缓冲的代理对象");
    }

    /**
     * 根据上下文配置代理对象,超时的时间设置
     */
    private Object configurationProxy(Object proxy, Context context) {
        setConnectionTimeout(proxy, context.getConnectionTimeout());
        setReceiveTimeout(proxy, context.getReceiveTimeout());
        setSynchronousTimeout(proxy, context.getSynchronousTimeout());
        return proxy;
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

    private static class JaxWsContextKey {

        private String address;
        private String serviceName;
        private String username;
        private String password;

        public JaxWsContextKey(Context context) {
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
