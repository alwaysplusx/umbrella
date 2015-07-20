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
package com.harmony.umbrella.ws;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.jws.WebService;
import javax.ws.rs.Path;

import org.apache.cxf.endpoint.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.util.Assert;
import com.harmony.umbrella.util.ClassUtils;
import com.harmony.umbrella.util.Exceptions;
import com.harmony.umbrella.util.StringUtils;
import com.harmony.umbrella.ws.cxf.ServerImpl;
import com.harmony.umbrella.ws.cxf.interceptor.MessageInInterceptor;
import com.harmony.umbrella.ws.cxf.interceptor.MessageOutInterceptor;
import com.harmony.umbrella.ws.jaxrs.JaxRsServerBuilder;
import com.harmony.umbrella.ws.jaxrs.JaxRsServerBuilder.BeanFactoryProvider;
import com.harmony.umbrella.ws.jaxrs.JaxRsServerBuilder.JaxRsServerFactoryConfig;
import com.harmony.umbrella.ws.jaxws.JaxWsServerBuilder;
import com.harmony.umbrella.ws.jaxws.JaxWsServerBuilder.BeanFactoryInvoker;
import com.harmony.umbrella.ws.jaxws.JaxWsServerBuilder.JaxWsServerFactoryConfig;

/**
 * 服务管理实例
 * FIXME 修改服务发布时候不能准确定位到是何种类型的服务
 * 
 * @author wuxii@foxmail.com
 */
public class ServerManager {

    private static final Logger log = LoggerFactory.getLogger(ServerManager.class);
    /**
     * 所有的服务
     */
    private static final Map<ServerKey, ServerImpl> servers = new HashMap<ServerKey, ServerImpl>();

    /**
     * 服务元数据加载器
     */
    private MetadataLoader metaLoader;

    /**
     * @see org.apache.cxf.service.invoker.Invoker
     */
    private BeanFactoryInvoker beanFactoryInvoker;

    /**
     * @see org.apache.cxf.jaxrs.lifecycle.ResourceProvider
     */
    private BeanFactoryProvider beanFactoryProvider;

    /**
     * 当遇到服务发布失败, 抛出异常终止发布
     */
    private boolean failFast = false;

    /**
     * 服务instance
     */
    private static ServerManager INSTANCE;

    private ServerManager() {
    }

    public static ServerManager getServerManager() {
        if (INSTANCE == null) {
            synchronized (ServerManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ServerManager();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 发布一个服务实例为{@code serviceClass}的服务，地址依赖{@link #metaLoader}
     * 来加载。所以在使用这个方法时候请注意需要先设置{@link #metaLoader}
     *
     * @param clazz
     *            服务实例类型
     */
    public boolean publish(Class<?> clazz) {
        if (isJaxRsService(clazz)) {
            return publish(clazz, null, (JaxRsServerFactoryConfig) null);
        } else if (isJaxWsService(clazz)) {
            return publish(clazz, null, (JaxWsServerFactoryConfig) null);
        } else {
            throw new IllegalArgumentException("service class neither jaxws service nor jaxrs service");
        }
    }

    /**
     * 在指定地址发布一个服务
     *
     * @param clazz
     *            服务类型
     * @param address
     *            服务地址
     */
    public boolean publish(Class<?> clazz, String address) {
        try {
            if (isJaxRsService(clazz) && isJaxWsService(clazz)) {
                publish(clazz, address, (JaxRsServerFactoryConfig) null);
                publish(clazz, address, (JaxWsServerFactoryConfig) null);
                return true;
            } else if (isJaxWsService(clazz)) {
                return publish(clazz, address, (JaxWsServerFactoryConfig) null);
            } else if (isJaxRsService(clazz)) {
                return publish(clazz, address, (JaxRsServerFactoryConfig) null);
            }
        } catch (Exception e) {
            try {
                destory(address);
            } catch (Exception e1) {
            }
            throw Exceptions.unchecked(e);
        }
        throw new IllegalArgumentException("service class neither jaxws service nor jaxrs service");
    }

    /**
     * 可配置的发布服务
     *
     * @param serviceClass
     *            服务类型
     * @param factoryConfig
     *            服务配置回调
     */
    public boolean publish(Class<?> serviceClass, JaxWsServerFactoryConfig factoryConfig) {
        return publish(serviceClass, null, factoryConfig);
    }

    /**
     * 可配置的发布服务
     *
     * @param resourceClass
     *            服务类型
     * @param factoryConfig
     *            服务配置回调
     */
    public boolean publish(Class<?> resourceClass, JaxRsServerFactoryConfig factoryConfig) {
        return publish(resourceClass, null, factoryConfig);
    }

    /**
     * 在指定地址发布服务，发布前提供配置服务
     *
     * @param serviceClass
     *            服务类型
     * @param address
     *            服务地址
     * @param factoryConfig
     *            服务配置回调
     */
    public boolean publish(Class<?> serviceClass, String address, JaxWsServerFactoryConfig factoryConfig) {
        return doPublish(serviceClass, address, factoryConfig);
    }

    public boolean publish(Class<?> resourceClass, String address, JaxRsServerFactoryConfig factoryConfig) {
        return doPublish(resourceClass, address, factoryConfig);
    }

    private boolean doPublish(Class<?> serviceClass, String address, JaxWsServerFactoryConfig factoryConfig) {
        Assert.notNull(serviceClass, "service class is null");
        try {
            JaxWsServerBuilder builder = JaxWsServerBuilder.create()//
                    .setBeanFactoryInvoker(beanFactoryInvoker)//
                    .addInInterceptor(new MessageInInterceptor("JaxWs-Server Inbound"))//
                    .addOutInterceptor(new MessageOutInterceptor("JaxWs-Server Outbound"));

            Metadata metadata = getMetadata(serviceClass);
            if (metadata != null) {
                address = StringUtils.isNotBlank(address) ? address : metadata.getAddress();
                builder.setUsername(metadata.getUsername()).setPassword(metadata.getPassword());
            }

            Server server = builder.publish(serviceClass, address, factoryConfig);

            // register server
            registerServer(new ServerImpl(serviceClass, server), address);

            log.info("success publish server[serviceClass:{},address:{}]", serviceClass.getName(), address);
        } catch (Exception e) {
            log.error("failed publish server[serviceClass:{},address:{}]", serviceClass.getName(), address, e);
            if (failFast) {
                throw Exceptions.unchecked(e);
            }
            return false;
        }
        return true;
    }

    private boolean doPublish(Class<?> resourceClass, String address, JaxRsServerFactoryConfig factoryConfig) {
        Assert.notNull(resourceClass, "resource class is null");
        try {

            JaxRsServerBuilder builder = JaxRsServerBuilder.create()//
                    .setProvider(beanFactoryProvider)//
                    .addInInterceptor(new MessageInInterceptor("REST-Server Inbound"))//
                    .addOutInterceptor(new MessageOutInterceptor("REST-Server Outbound"));

            Metadata metadata = getMetadata(resourceClass);
            if (metadata != null) {
                address = StringUtils.isNotBlank(address) ? address : metadata.getAddress();
                builder.setUsername(metadata.getUsername()).setPassword(metadata.getPassword());
            }

            Server server = builder.publish(resourceClass, address, factoryConfig);

            // register server
            registerServer(new ServerImpl(resourceClass, server), address);

            log.info("success publish server[{}@{}]", resourceClass.getName(), address);
        } catch (Exception e) {
            log.error("failed publish server[{}@{}]", resourceClass.getName(), address, e);
            if (failFast) {
                throw Exceptions.unchecked(e);
            }
            return false;
        }
        return true;
    }

    private Metadata getMetadata(Class<?> clazz) {
        return metaLoader == null ? null : metaLoader.getMetadata(clazz);
    }

    /**
     * 设置服务的元数据加载器
     */
    public void setMetadataLoader(MetadataLoader metaLoader) {
        this.metaLoader = metaLoader;
    }

    /**
     * @see org.apache.cxf.service.invoker.Invoker
     */
    public void setBeanFactoryInvoker(BeanFactoryInvoker beanFactoryInvoker) {
        this.beanFactoryInvoker = beanFactoryInvoker;
    }

    /**
     * @see org.apache.cxf.jaxrs.lifecycle.ResourceProvider
     */
    public void setBeanFactoryProvider(BeanFactoryProvider beanFactoryProvider) {
        this.beanFactoryProvider = beanFactoryProvider;
    }

    /**
     * 设置快速失败策略
     */
    public void setFailFast(boolean failFast) {
        this.failFast = failFast;
    }

    /**
     * 检测地址是否已经发布了服务
     *
     * @param address
     *            检测的地址
     */
    public boolean inUse(String address) {
        Iterator<ServerKey> it = servers.keySet().iterator();
        while (it.hasNext()) {
            if (it.next().address.equals(address)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取类所发布的所有服务地址
     * 
     * @param clazz
     *            服务类
     * @return 所发布的所有地址
     */
    public String[] publishAddresses(Class<?> clazz) {
        List<String> addresses = new ArrayList<String>();
        Iterator<ServerKey> it = servers.keySet().iterator();
        while (it.hasNext()) {
            ServerKey key = it.next();
            if (key.clazz == clazz) {
                addresses.add(key.address);
            }
        }
        return addresses.toArray(new String[addresses.size()]);
    }

    private void registerServer(ServerImpl server, String address) {
        ServerKey key = new ServerKey(server.getServiceClass(), address);
        servers.put(key, server);
    }

    private void unregisterServer(Map<ServerKey, ServerImpl> serverMap) {
        Iterator<Entry<ServerKey, ServerImpl>> it = serverMap.entrySet().iterator();
        while (it.hasNext()) {
            Entry<ServerKey, ServerImpl> entry = it.next();
            ServerKey key = entry.getKey();
            ServerImpl server = entry.getValue();
            server.stop();
            server.destroy();
            log.info("stop service[{}@{}]", key.clazz.getName(), key.address);
            if (servers == serverMap) {
                it.remove();
            } else {
                servers.remove(key);
            }
        }
    }

    /**
     * 销毁指定地址的服务实例
     *
     * @param address
     */
    public void destory(String address) {
        unregisterServer(getServer(address));
    }

    /**
     * 销毁所有clazz的服务实例
     *
     * @param clazz
     */
    public void destory(Class<?> clazz) {
        unregisterServer(getServer(clazz));
    }

    public void destory(String address, Class<?> clazz) {
        unregisterServer(getServer(address, clazz));
    }

    /**
     * 销毁所有服务实例
     */
    public void destoryAll() {
        unregisterServer(servers);
    }

    private Map<ServerKey, ServerImpl> getServer(String address) {
        Map<ServerKey, ServerImpl> result = new HashMap<ServerKey, ServerImpl>();
        Iterator<ServerKey> it = servers.keySet().iterator();
        while (it.hasNext()) {
            ServerKey key = it.next();
            if (key.address.equals(address)) {
                result.put(key, servers.get(key));
            }
        }
        return result;
    }

    private Map<ServerKey, ServerImpl> getServer(Class<?> clazz) {
        Map<ServerKey, ServerImpl> result = new HashMap<ServerKey, ServerImpl>();
        Iterator<ServerKey> it = servers.keySet().iterator();
        while (it.hasNext()) {
            ServerKey key = it.next();
            if (key.clazz == clazz) {
                result.put(key, servers.get(key));
            }
        }
        return result;
    }

    private Map<ServerKey, ServerImpl> getServer(String address, Class<?> clazz) {
        Map<ServerKey, ServerImpl> result = new HashMap<ServerKey, ServerImpl>();
        ServerKey key = new ServerKey(clazz, address);
        Iterator<ServerKey> it = servers.keySet().iterator();
        while (it.hasNext()) {
            if (key.equals(it.next())) {
                result.put(key, servers.get(key));
            }
        }
        return result;
    }

    protected static boolean isJaxRsClass(Class<?> clazz) {
        return clazz.getAnnotation(Path.class) != null ? true : false;
    }

    protected static boolean isJaxWsClass(Class<?> clazz) {
        return clazz.getAnnotation(WebService.class) != null ? true : false;
    }

    public static boolean isJaxRsService(Class<?> clazz) {
        if (isJaxRsClass(clazz)) {
            return true;
        }
        Class<?>[] classes = ClassUtils.getAllInterfaces(clazz);
        for (Class<?> clas : classes) {
            if (clas.getAnnotation(Path.class) != null) {
                return true;
            }
        }
        return false;
    }

    public static boolean isJaxWsService(Class<?> clazz) {
        if (isJaxWsClass(clazz)) {
            return true;
        }
        Class<?>[] classes = ClassUtils.getAllInterfaces(clazz);
        for (Class<?> clas : classes) {
            if (clas.getAnnotation(WebService.class) != null) {
                return true;
            }
        }
        return false;
    }

    private static class ServerKey {

        private final Class<?> clazz;
        private final String address;

        public ServerKey(Class<?> clazz, String address) {
            this.clazz = clazz;
            this.address = address;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((address == null) ? 0 : address.hashCode());
            result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
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
            ServerKey other = (ServerKey) obj;
            if (address == null) {
                if (other.address != null)
                    return false;
            } else if (!address.equals(other.address))
                return false;
            if (clazz == null) {
                if (other.clazz != null)
                    return false;
            } else if (!clazz.equals(other.clazz))
                return false;
            return true;
        }

    }

}
