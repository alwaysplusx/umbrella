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

    /**
     * 所有服务缓存， key=address, value=Server
     */
    private final Map<String, Server> servers = new HashMap<String, Server>();

    /**
     * 服务元数据加载器
     */
    private MetadataLoader metadataLoader;

    protected ServerManager() {
    }

    /**
     * 发布服务
     * <p>
     * {@code serviceClass}, {@code serviceBean}必须制定一个。如果指定了{@code serviceBean}
     * 则服务为单例。指定则有工厂bean决定服务的bean
     * 
     * @param serviceClass
     *            服务类型
     * @param serviceBean
     *            服务实例
     * @param address
     *            服务发布的地址
     * @param factoryConfig
     *            服务工厂配置器
     * @return 服务
     */
    protected abstract Server doPublish(Class<?> serviceClass, Object serviceBean, String address, FactoryConfig<T> factoryConfig);

    /**
     * 发布{@code service}服务，地址依赖{@link #metadataLoader MetadataLoader} 来加载。
     * <p>
     * <b>使用本方法时候确保先设置{@link #metadataLoader MetadataLoader}
     * ，否则无法正确设置要发布服务的地址</b>
     * </p>
     *
     * @param service
     *            服务
     * @return 发布的服务
     */
    public Server publish(Object service) {
        return publish(service, loadAddress(service));
    }

    /**
     * 在指定地址{@code address}发布{@code service}服务
     * 
     * @param service
     *            服务
     * @param address
     *            服务地址
     * @return 发布的服务
     */
    public Server publish(Object service, String address) {
        Assert.notBlank(address, "address is null or blank");
        return publish(service, address, null);
    }

    /**
     * 可配置的发布服务, 在发布前使用{@code factoryConfig}配置服务工厂bean
     *
     * @param service
     *            服务
     * @param factoryConfig
     *            服务配置器
     * @return 发布的服务
     */
    public Server publish(Object service, FactoryConfig<T> factoryConfig) {
        return publish(service, loadAddress(service), factoryConfig);
    }

    /**
     * 在指定的地址发布服务， 发布前配置服务工程bean
     *
     * @param service
     *            服务
     * @param address
     *            服务地址
     * @param factoryConfig
     *            服务配置器
     * @return 发布的服务
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

    /**
     * 检测服务地址是否被占用
     * 
     * <pre>
     *  1) 
     *     已发布的服务地址: http://localhost:8080/user/save
     *      
     *      isUse("http://localhost:8080/user") = true
     *  2)
     *      已发布的服务地址 : http://localhost:8080/user
     * 
     *      isUse("http://localhost:8080/user/save") = true
     * </pre>
     * 
     * @param address
     *            服务地址
     * @return true服务被占用，false未被占用
     */
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

    /**
     * 根据服务类加载服务发布的地址
     * 
     * @param service
     *            服务类
     * @return 服务发布地址
     */
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
