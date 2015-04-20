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
package com.harmony.umbrella.jaxws;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.cxf.frontend.ServerFactoryBean;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.core.BeanLoader;
import com.harmony.umbrella.core.ClassBeanLoader;
import com.harmony.umbrella.jaxws.impl.SimpleJaxWsMetadata;
import com.harmony.umbrella.util.StringUtils;

/**
 * 基于cxf的WebService服务管理
 * 
 * @author wuxii@foxmail.com
 */
public class JaxWsServerManager {

    private static final Logger log = LoggerFactory.getLogger(JaxWsServerManager.class);
    /**
     * 一个服务serviceClass对应一个服务信息
     */
    private static final Map<Class<?>, JaxWsServer> servers = new HashMap<Class<?>, JaxWsServer>();

    private static JaxWsServerManager instance;
    private BeanLoader beanLoader = new ClassBeanLoader();

    private JaxWsServerManager() {
    }

    public static final JaxWsServerManager getInstance() {
        if (instance == null) {
            synchronized (JaxWsServerManager.class) {
                if (instance == null) {
                    instance = new JaxWsServerManager();
                }
            }
        }
        return instance;
    }

    /**
     * 在指定地址发布一个服务
     * 
     * @param serviceClass
     * @param address
     * @return
     */
    public boolean publish(Class<?> serviceClass, String address) {
        return publish(serviceClass, address, null);
    }

    /**
     * 使用指定的服务元数据发布服务
     * 
     * @param serviceClass
     * @param metadataLoader
     * @return
     */
    public boolean publish(Class<?> serviceClass, MetadataLoader metadataLoader) {
        return publish(serviceClass, metadataLoader, null);
    }

    /**
     * 在指定地址发布服务，发布前提供配置服务
     * 
     * @param serviceClass
     * @param address
     * @param factoryConfig
     * @return
     */
    public boolean publish(Class<?> serviceClass, String address, JaxWsServerFactoryConfig factoryConfig) {
        Object serviceBean = createServiceBean(serviceClass);
        return doPublish(serviceClass, serviceBean, new SimpleJaxWsMetadata(serviceClass, address), factoryConfig);
    }

    /**
     * 可配置的发布服务
     * 
     * @param serviceClass
     * @param metadataLoader
     * @param factoryConfig
     * @return
     */
    public boolean publish(Class<?> serviceClass, MetadataLoader metadataLoader, JaxWsServerFactoryConfig factoryConfig) {
        Object serviceBean = createServiceBean(serviceClass);
        JaxWsMetadata metadata = metadataLoader.getJaxWsMetadata(serviceClass);
        return doPublish(serviceClass, serviceBean, metadata, factoryConfig);
    }

    private boolean doPublish(Class<?> serviceClass, Object serviceBean, JaxWsMetadata metadata, JaxWsServerFactoryConfig factoryConfig) {
        String address = metadata.getAddress();
        if (StringUtils.isBlank(address)) {
            throw new IllegalArgumentException("service address is null or blank");
        }
        // 地址已经被使用
        if (inUse(address)) {
            if (isPublished(serviceClass, address)) {
                // 发布了相同的服务 update service
                return true;
            }
            // 不同的服务占用了地址
            log.error("publish two different service in same address {}", address);
            return false;
        }
        JaxWsServerFactoryBean factoryBean = new JaxWsServerFactoryBean();
        if (factoryConfig != null) {
            factoryConfig.config(factoryBean);
            if (factoryBean.getServer() != null) {
                log.error("config factory not allow to create server");
                throw new IllegalStateException("config factory not allow to create server");
            }
        }
        factoryBean.setAddress(address);
        factoryBean.setServiceBean(serviceBean);
        factoryBean.create();
        registerServer(serviceClass, factoryBean);
        log.debug("publish jax-ws service[{}] successfully", serviceClass.getName());
        return true;
    }

    /**
     * 检测地址是否已经发布了服务
     * 
     * @param address
     * @return
     */
    public boolean inUse(String address) {
        for (JaxWsServer server : servers.values()) {
            if (server.inUse(address))
                return true;
        }
        return false;
    }

    /**
     * 检查是否在地址上发布了指定类型的服务
     * 
     * @param serviceClass
     * @param address
     * @return
     */
    public boolean isPublished(Class<?> serviceClass, String address) {
        if (servers.containsKey(serviceClass)) {
            return servers.get(serviceClass).inUse(address);
        }
        return false;
    }

    protected Object createServiceBean(Class<?> serviceClass) {
        return beanLoader.loadBean(serviceClass, BeanLoader.PROTOTYPE);
    }

    /**
     * 销毁指定地址的服务实例
     * 
     * @param serviceClass
     * @param address
     */
    public void destory(String address) {
        for (JaxWsServer server : servers.values()) {
            server.removeInstance(address);
            if (!server.hasServiceInstance()) {
                servers.remove(server.getServiceClass());
            }
        }
    }

    /**
     * 销毁所有serviceClass的服务实例
     * 
     * @param serviceClass
     */
    public void destory(Class<?> serviceClass) {
        JaxWsServer server = servers.get(serviceClass);
        for (String address : server.listAddress()) {
            server.removeInstance(address);
        }
        servers.remove(serviceClass);
    }

    /**
     * 销毁所有服务实例
     */
    public void destoryAll() {
        for (Class<?> serviceClass : servers.keySet()) {
            destory(serviceClass);
        }
    }

    private void registerServer(Class<?> serviceClass, JaxWsServerFactoryBean factoryBean) {
        JaxWsServer server = null;
        if (!servers.containsKey(serviceClass)) {
            servers.put(serviceClass, server = new JaxWsServer(serviceClass));
        }
        server.addServiceInstance(factoryBean.getAddress(), factoryBean);
    }

    public BeanLoader getBeanLoader() {
        return beanLoader;
    }

    public void setBeanLoader(BeanLoader beanLoader) {
        this.beanLoader = beanLoader;
    }

    /**
     * 单个服务的所有信息.
     * <p>
     * 单个服务可以发布在多个地址.不同的地址有不同的服务实例
     * 
     * @author wuxii@foxmail.com
     */
    private class JaxWsServer {

        private final Class<?> serviceClass;
        // 用链式的结构获取对应的资源.根元素为服务的地址
        // address[i] -> serviceBean[i] -> JaxWsServerFactoryBean[i]
        private Map<Object, Object> resources = new HashMap<Object, Object>();

        public JaxWsServer(Class<?> serviceClass) {
            this.serviceClass = serviceClass;
        }

        public Class<?> getServiceClass() {
            return serviceClass;
        }

        public void removeInstance(String address) {
            getServerFactoryBean(address).destroy();
            resources.remove(getServiceBean(address));
            resources.remove(address);
        }

        public void addServiceInstance(String address, JaxWsServerFactoryBean factoryBean) {
            resources.put(factoryBean.getAddress(), factoryBean.getServiceBean());
            resources.put(factoryBean.getServiceBean(), factoryBean);
        }

        public boolean hasServiceInstance() {
            return !resources.isEmpty();
        }

        /**
         * 检查是否在指定地址发布了服务
         * 
         * @param address
         * @return
         */
        public boolean inUse(String address) {
            return resources.containsKey(address);
        }

        /**
         * 该服务所有的服务地址
         * 
         * @return
         */
        public List<String> listAddress() {
            List<String> addresses = new ArrayList<String>();
            Iterator<Object> iterator = resources.keySet().iterator();
            for (; iterator.hasNext();) {
                Object obj = iterator.next();
                if (obj instanceof String) {
                    addresses.add((String) obj);
                }
            }
            return Collections.unmodifiableList(addresses);
        }

        public Object getServiceBean(String address) {
            return resources.get(address);
        }

        public ServerFactoryBean getServerFactoryBean(String address) {
            return (ServerFactoryBean) resources.get(getServiceBean(address));
        }

    }

    /**
     * 服务创建前的回调配置
     * 
     * @author wuxii@foxmail.com
     */
    public interface JaxWsServerFactoryConfig {

        void config(JaxWsServerFactoryBean factoryBean);

    }

}
