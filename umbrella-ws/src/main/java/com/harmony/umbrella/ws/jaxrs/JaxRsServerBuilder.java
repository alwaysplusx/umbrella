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
package com.harmony.umbrella.ws.jaxrs;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.ResourceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.core.BeanFactory;
import com.harmony.umbrella.core.NoSuchBeanFindException;
import com.harmony.umbrella.util.Assert;
import com.harmony.umbrella.util.StringUtils;
import com.harmony.umbrella.ws.Metadata;
import com.harmony.umbrella.ws.MetadataLoader;
import com.harmony.umbrella.ws.WebServiceException;
import com.harmony.umbrella.ws.cxf.SimpleBeanFactoryProvider;
import com.harmony.umbrella.ws.cxf.interceptor.MessageInInterceptor;
import com.harmony.umbrella.ws.cxf.interceptor.MessageOutInterceptor;
import com.harmony.umbrella.ws.support.SimpleMetadata;

/**
 * @author wuxii@foxmail.com
 */
public class JaxRsServerBuilder {

    private static final Logger log = LoggerFactory.getLogger(JaxRsServerBuilder.class);

    protected Class<?> resourceClass;

    /**
     * 发布的地址
     */
    protected String address;

    /**
     * 访问服务的用户名
     */
    protected String username;

    /**
     * 访问服务的密码
     */
    protected String password;

    private JAXRSServerFactoryBean serverFactoryBean;

    private BeanFactoryProvider provider;

    /**
     * JaxWs服务元数据加载器
     */
    private MetadataLoader metaLoader;

    protected JaxRsServerBuilder() {

    }

    public static JaxRsServerBuilder create() {
        return new JaxRsServerBuilder();
    }

    public Server publish() {
        return publish(resourceClass, address, null);
    }

    public Server publish(Class<?> resourceClass) {
        return publish(resourceClass, address, null);
    }

    public Server publish(Class<?> resourceClass, String address) {
        return publish(resourceClass, address, null);
    }

    public Server publish(Class<?> resourceClass, String address, JaxRsServerFactoryConfig factoryConfig) {
        this.resourceClass = resourceClass;
        this.address = address;
        return doPublish(resourceClass, factoryConfig);
    }

    protected Server doPublish(Class<?> resourceClass, JaxRsServerFactoryConfig factoryConfig) {
        Assert.notNull(resourceClass, "resource class is null");
        Metadata metadata = getMetadata(resourceClass);
        Assert.isTrue(StringUtils.isNotBlank(metadata.getAddress()), "server address is null or blank");
        try {
            serverFactoryBean = new JAXRSServerFactoryBean();
            if (factoryConfig != null) {
                factoryConfig.config(serverFactoryBean);
                if (serverFactoryBean.getServer() != null) {
                    throw new IllegalStateException("config factory not allow call create method");
                }
            }
            serverFactoryBean.getInInterceptors().add(new MessageInInterceptor());
            serverFactoryBean.getOutInterceptors().add(new MessageOutInterceptor());
            serverFactoryBean.setAddress(address);
            serverFactoryBean.setResourceClasses(resourceClass);
            serverFactoryBean.setResourceProvider(getProvider());
            Server server = serverFactoryBean.create();
            log.debug("create server success");
            return server;
        } catch (NoSuchBeanFindException e) {
            throw new WebServiceException("no target bean of class " + resourceClass.getName() + " find");
        }
    }

    /**
     * 设置服务地址
     * 
     * @param address
     *            服务地址
     * @return current builder
     */
    public JaxRsServerBuilder setAddress(String address) {
        this.address = address;
        return this;
    }

    /**
     * 设置服务用户名
     * 
     * @param username
     *            用户名
     * @return current builder
     */
    public JaxRsServerBuilder setUsername(String username) {
        this.username = username;
        return this;
    }

    /**
     * 设置服务密码
     * 
     * @param password
     *            用户密码
     * @return current builder
     */
    public JaxRsServerBuilder setPassword(String password) {
        this.password = password;
        return this;
    }

    public JaxRsServerBuilder setResourceClass(Class<?> resourceClass) {
        this.resourceClass = resourceClass;
        return this;
    }

    public JaxRsServerBuilder setProvider(BeanFactoryProvider provider) {
        Assert.notNull(provider, "resource provider must not be null");
        this.provider = provider;
        return this;
    }

    protected BeanFactoryProvider getProvider() {
        if (provider == null) {
            provider = new SimpleBeanFactoryProvider(resourceClass);
        }
        return provider;
    }

    protected Metadata getMetadata(Class<?> serviceClass) {
        if (metaLoader == null) {
            return new SimpleMetadata(serviceClass, address, username, password);
        }
        SimpleMetadata result = new SimpleMetadata(serviceClass);
        Metadata temp = metaLoader.getMetadata(serviceClass);
        result.setAddress(StringUtils.isBlank(address) ? temp.getAddress() : address);
        result.setUsername(StringUtils.isNotBlank(username) ? temp.getUsername() : username);
        result.setPassword(StringUtils.isNotBlank(password) ? temp.getPassword() : password);
        result.setServiceName(temp.getServiceName());
        return result;
    }

    public JaxRsServerBuilder setMetadataLoader(MetadataLoader metaLoader) {
        this.metaLoader = metaLoader;
        return this;
    }

    /**
     * 获取builder中的信息
     * 
     * @param cls
     *            待获取的类
     * @return 指定的类信息
     * @throws IllegalArgumentException
     *             不支持的类型
     */
    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> cls) {
        if (JAXRSServerFactoryBean.class.isAssignableFrom(cls)) {
            return (T) serverFactoryBean;
        }
        throw new IllegalArgumentException("Unsupported unwrap target type [" + cls.getName() + "]");
    }

    public interface JaxRsServerFactoryConfig {

        void config(JAXRSServerFactoryBean factoryBean);

    }

    public interface BeanFactoryProvider extends ResourceProvider, BeanFactory {

    }

}
