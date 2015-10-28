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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.ws.WebServiceException;

import com.harmony.umbrella.util.Assert;

/**
 * 服务管理, 负责管理所有的服务实例
 *
 * @param <T>
 *            创建服务的工厂类型 - 主要用于工厂配置选项
 * @author wuxii@foxmail.com
 */
public abstract class ServerManager<T> {

    private final Map<String, Server> servers = new HashMap<String, Server>();

    /**
     * 服务元数据加载器
     */
    private MetadataLoader metadataLoader;

    protected ServerManager() {
    }

    /**
     * 发布{@code service}服务，地址依赖{@link #metadataLoader} 来加载。
     * <p>
     * <b>使用本方法时候确保先设置{@link #metadataLoader}，否则无法正确设置要发布服务的地址
     * </p>
     *
     * @param service
     *            服务
     */
    public Server publish(Object service) {
        return publish(service, loadAddress(service));
    }

    /**
     * 在指定地址发布{@code service}服务
     * <p/>
     * 
     * @param service
     *            服务
     * @param address
     *            服务地址
     */
    public Server publish(Object service, String address) {
        Assert.notBlank(address, "address is null or blank");
        return publish(service, address, null);
    }

    /**
     * 可配置的发布服务
     *
     * @param service
     *            服务类型
     * @param factoryConfig
     *            服务配置
     */
    public Server publish(Object service, FactoryConfig<T> factoryConfig) {
        return publish(service, loadAddress(service), factoryConfig);
    }

    /**
     * 在指定的地址发布服务， 发布前提供配置服务
     *
     * @param service
     *            服务类型
     * @param address
     *            服务地址
     * @param factoryConfig
     *            服务的配置
     */
    public Server publish(Object service, String address, FactoryConfig<T> factoryConfig) {
        Assert.notNull(service, "publish service object is null");
        Server server = null;

        if (service instanceof Class) {
            server = doPublish((Class<?>) service, null, address, factoryConfig);
        } else {
            server = doPublish(service.getClass(), service, address, factoryConfig);
        }

        registerServer(address, server);

        return server;
    }

    protected abstract Server doPublish(Class<?> serviceClass, Object serviceBean, String address, FactoryConfig<T> factoryConfig);

    public boolean inUse(String address) {
        Assert.notNull(address, "address is null");
        Iterator<String> addresses = servers.keySet().iterator();
        while (addresses.hasNext()) {
            String serverAddress = addresses.next();
            // 两个地址有重叠部分则表示已经使用
            if (serverAddress.startsWith(address) //
                    || address.startsWith(serverAddress)) {
                return true;
            }
        }
        return false;
    }

    private String loadAddress(Object service) {
        Class<?> serviceClass = service instanceof Class ? (Class<?>) service : service.getClass();
        Metadata metadata = getMetadataLoader().loadMetadata(serviceClass);
        if (metadata != null) {
            return metadata.getAddress();
        }
        throw new WebServiceException("metadata loader not set, can't load service address");
    }

    private void registerServer(String address, Server server) {
        servers.put(address, server);
    }

    public void setMetadataLoader(MetadataLoader metadataLoader) {
        this.metadataLoader = metadataLoader;
    }

    public MetadataLoader getMetadataLoader() {
        return metadataLoader;
    }

}
