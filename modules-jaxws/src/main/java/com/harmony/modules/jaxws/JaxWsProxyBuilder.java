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
package com.harmony.modules.jaxws;

import java.lang.reflect.Proxy;
import java.util.List;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.frontend.ClientProxyFactoryBean;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.message.Message;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JaxWsProxyBuilder {

    private static final ThreadLocal<JaxWsProxyFactoryBean> factoryBeans = new ThreadLocal<JaxWsProxyFactoryBean>();
    private static final Logger log = LoggerFactory.getLogger(JaxWsProxyBuilder.class);
    private String address;
    private String username;
    private String password;
    private MetadataLoader loader;
    private Object target;

    private boolean loadUsername;
    private boolean loadPassword;
    private boolean loadAddress;

    public static JaxWsProxyBuilder newProxyBuilder() {
        refush();
        return new JaxWsProxyBuilder();
    }

    private static void refush() {
        JaxWsProxyFactoryBean factoryBean;
        if ((factoryBean = factoryBeans.get()) == null) {
            factoryBeans.set(factoryBean = new JaxWsProxyFactoryBean());
        }
        factoryBean.getInFaultInterceptors().clear();
        factoryBean.getInInterceptors().clear();
        factoryBean.getOutFaultInterceptors().clear();
        factoryBean.getOutInterceptors().clear();
        if (log.isInfoEnabled()) {
            factoryBean.getInInterceptors().add(new LoggingInInterceptor());
            factoryBean.getOutInterceptors().add(new LoggingOutInterceptor());
        }
    }

    public List<Interceptor<? extends Message>> getInInterceptors() {
        return factoryBeans.get().getInInterceptors();
    }

    public List<Interceptor<? extends Message>> getInFaultInterceptors() {
        return factoryBeans.get().getInFaultInterceptors();
    }

    public List<Interceptor<? extends Message>> getOutFaultInterceptors() {
        return factoryBeans.get().getOutFaultInterceptors();
    }

    public List<Interceptor<? extends Message>> getOutInterceptors() {
        return factoryBeans.get().getOutInterceptors();
    }

    public JaxWsProxyBuilder setAddress(String address) {
        this.address = address;
        this.loadAddress = address == null;
        return this;
    }

    public JaxWsProxyBuilder setUsername(String username) {
        this.username = username;
        this.loadUsername = false;
        return this;
    }

    public JaxWsProxyBuilder setPassword(String password) {
        this.password = password;
        this.loadPassword = false;
        return this;
    }

    public <T> T build(Class<T> serviceClass, JaxWsProxyFactoryConfig proxyConfig) {
        JaxWsProxyFactoryBean factoryBean = factoryBeans.get();
        String address = getAddress(serviceClass);
        if (address == null || "".equals(address))
            throw new IllegalArgumentException("proxy address is null or blank");
        if (proxyConfig != null)
            proxyConfig.config(factoryBean);
        factoryBean.setAddress(address);
        factoryBean.setUsername(getUsername(serviceClass));
        factoryBean.setPassword(getPassword(serviceClass));
        target = factoryBean.create(serviceClass);
        log.debug("build proxy[{}] successfully", serviceClass.getName());
        return serviceClass.cast(target);
    }

    public <T> T build(Class<T> serviceClass) {
        return build(serviceClass, null);
    }

    public <T> T build(Class<T> serviceClass, long connectionTimeout, JaxWsProxyFactoryConfig factoryConfig) {
        T proxy = build(serviceClass, null);
        setProxyTimeOut(proxy, connectionTimeout);
        return proxy;
    }

    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> cls) {
        if (ClientProxyFactoryBean.class.isAssignableFrom(cls)) {
            return (T) factoryBeans.get();
        }
        if (ClientProxy.class.isAssignableFrom(cls)) {
            if (target == null)
                throw new IllegalStateException("proxy not yet build");
            return (T) Proxy.getInvocationHandler(target);
        }
        if (Client.class.isAssignableFrom(cls)) {
            if (target == null)
                throw new IllegalStateException("proxy not yet build");
            return (T) ClientProxy.getClient(target);
        }
        throw new IllegalArgumentException("Unsupported unwrap target type [" + cls.getName() + "]");
    }

    public JaxWsProxyBuilder setMetadataLoader(MetadataLoader loader) {
        this.loader = loader;
        this.loadAddress = this.loadPassword = this.loadUsername = loader != null;
        return this;
    }

    public <T> T build(Class<T> serviceClass, long connectionTimeout) {
        T target = build(serviceClass);
        setProxyTimeOut(target, connectionTimeout);
        return target;
    }

    public static void setProxyTimeOut(Object target, long connectionTimeout) {
        Client proxy = ClientProxy.getClient(target);
        HTTPConduit conduit = (HTTPConduit) proxy.getConduit();
        HTTPClientPolicy policy = new HTTPClientPolicy();
        policy.setConnectionTimeout(connectionTimeout);
        // policy.setReceiveTimeout(receiveTimeout);
        conduit.setClient(policy);
    }

    protected String getAddress(Class<?> serviceClass) {
        if (loadAddress)
            return loader.getAddress(serviceClass);
        return address;
    }

    protected String getUsername(Class<?> serviceClass) {
        if (loadUsername)
            return loader.getUsername(serviceClass);
        return username;
    }

    protected String getPassword(Class<?> serviceClass) {
        if (loadPassword)
            return loader.getUsername(serviceClass);
        return password;
    }

    public interface JaxWsProxyFactoryConfig {

        void config(JaxWsProxyFactoryBean factoryBean);

    }

}
