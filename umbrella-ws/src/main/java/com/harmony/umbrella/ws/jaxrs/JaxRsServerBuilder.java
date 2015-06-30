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

import java.util.List;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.ResourceProvider;
import org.apache.cxf.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.core.BeanFactory;
import com.harmony.umbrella.util.Assert;
import com.harmony.umbrella.util.StringUtils;
import com.harmony.umbrella.ws.cxf.SimpleBeanFactoryProvider;

/**
 * @author wuxii@foxmail.com
 */
public class JaxRsServerBuilder {

    private static final Logger log = LoggerFactory.getLogger(JaxRsServerBuilder.class);

    private final JAXRSServerFactoryBean serverFactoryBean;

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

    private BeanFactoryProvider provider;

    protected JaxRsServerBuilder() {
        this.serverFactoryBean = new JAXRSServerFactoryBean();
    }

    public static JaxRsServerBuilder create() {
        return new JaxRsServerBuilder();
    }

    private List<Interceptor<? extends Message>> getInInterceptors() {
        return serverFactoryBean.getInInterceptors();
    }

    private List<Interceptor<? extends Message>> getOutInterceptors() {
        return serverFactoryBean.getOutInterceptors();
    }

    private List<Interceptor<? extends Message>> getInFaultInterceptors() {
        return serverFactoryBean.getInFaultInterceptors();
    }

    private List<Interceptor<? extends Message>> getOutFaultInterceptors() {
        return serverFactoryBean.getOutFaultInterceptors();
    }

    public JaxRsServerBuilder addInInterceptor(Interceptor<? extends Message> interceptor) {
        getInInterceptors().add(interceptor);
        return this;
    }

    public JaxRsServerBuilder addOutInterceptor(Interceptor<? extends Message> interceptor) {
        getOutInterceptors().add(interceptor);
        return this;
    }

    public JaxRsServerBuilder addInFaultInterceptor(Interceptor<? extends Message> interceptor) {
        getInFaultInterceptors().add(interceptor);
        return this;
    }

    public JaxRsServerBuilder addOutFaultInterceptor(Interceptor<? extends Message> interceptor) {
        getOutFaultInterceptors().add(interceptor);
        return this;
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
        Assert.isTrue(StringUtils.isNotBlank(address), "server address is null or blank");

        if (factoryConfig != null) {
            factoryConfig.config(serverFactoryBean);
            if (serverFactoryBean.getServer() != null) {
                throw new IllegalStateException("config factory not allow call create method");
            }
        }

        serverFactoryBean.setAddress(address);
        serverFactoryBean.setResourceClasses(resourceClass);
        serverFactoryBean.setResourceProvider(getProvider());
        Server server = serverFactoryBean.create();

        log.debug("create server[{}@{}] success", resourceClass.getName(), address);
        return server;
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
        this.provider = provider;
        return this;
    }

    protected BeanFactoryProvider getProvider() {
        if (provider == null) {
            provider = new SimpleBeanFactoryProvider(resourceClass);
        }
        return provider;
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
