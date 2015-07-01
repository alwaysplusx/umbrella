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

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.jaxrs.client.Client;
import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.apache.cxf.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.util.Assert;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public class JaxRsClientBuilder {

    private static final Logger log = LoggerFactory.getLogger(JaxRsClientBuilder.class);

    private final JAXRSClientFactoryBean clientFactoryBean;

    private String address;
    private String username;
    private String password;

    private long timeToKeepState;

    private boolean inheritHeaders;

    private final Map<String, String> headers = new HashMap<String, String>();

    private boolean threadSafe = true;

    private Object target;

    public JaxRsClientBuilder() {
        this.clientFactoryBean = new JAXRSClientFactoryBean();
    }

    public static JaxRsClientBuilder create() {
        return new JaxRsClientBuilder();
    }

    public List<Interceptor<? extends Message>> getInInterceptors() {
        return clientFactoryBean.getInInterceptors();
    }

    public List<Interceptor<? extends Message>> getInFaultInterceptors() {
        return clientFactoryBean.getInFaultInterceptors();
    }

    public List<Interceptor<? extends Message>> getOutFaultInterceptors() {
        return clientFactoryBean.getOutFaultInterceptors();
    }

    public List<Interceptor<? extends Message>> getOutInterceptors() {
        return clientFactoryBean.getOutInterceptors();
    }

    public JaxRsClientBuilder addInInterceptor(Interceptor<? extends Message> interceptor) {
        getInInterceptors().add(interceptor);
        return this;
    }

    public JaxRsClientBuilder addOutInterceptor(Interceptor<? extends Message> interceptor) {
        getOutInterceptors().add(interceptor);
        return this;
    }

    public JaxRsClientBuilder addInFaultInterceptor(Interceptor<? extends Message> interceptor) {
        getInFaultInterceptors().add(interceptor);
        return this;
    }

    public JaxRsClientBuilder addOutFaultInterceptor(Interceptor<? extends Message> interceptor) {
        getOutFaultInterceptors().add(interceptor);
        return this;
    }

    public <T> T build(Class<T> resourceClass) {
        return build(resourceClass, address);
    }

    public <T> T build(Class<T> resourceClass, String address) {
        this.address = address;
        return build(resourceClass, (JaxRsClientFactoryConfig) null);
    }

    public <T> T build(Class<T> resourceClass, long timeToKeepState) {
        this.timeToKeepState = timeToKeepState;
        return build(resourceClass, (JaxRsClientFactoryConfig) null);
    }

    public <T> T build(Class<T> resourceClass, JaxRsClientFactoryConfig clientConfig) {

        Assert.notNull(resourceClass, "service class must be not null");
        Assert.isTrue(StringUtils.isNotBlank(address), "proxy address is null or blank");

        if (clientConfig != null) {
            clientConfig.config(clientFactoryBean);
        }

        clientFactoryBean.setResourceClass(resourceClass);
        clientFactoryBean.setAddress(address);
        clientFactoryBean.setUsername(username);
        clientFactoryBean.setPassword(password);
        clientFactoryBean.setHeaders(headers);
        clientFactoryBean.setInheritHeaders(inheritHeaders);
        clientFactoryBean.setThreadSafe(threadSafe);
        clientFactoryBean.setSecondsToKeepState(timeToKeepState);

        target = clientFactoryBean.create(resourceClass);

        log.debug("build rest client[{}] successfully", resourceClass.getName());

        return resourceClass.cast(target);
    }

    public JaxRsClientBuilder setAddress(String address) {
        this.address = address;
        return this;
    }

    public JaxRsClientBuilder setUsername(String username) {
        this.username = username;
        return this;
    }

    public JaxRsClientBuilder setPassword(String password) {
        this.password = password;
        return this;
    }

    public JaxRsClientBuilder setSecondsToKeepState(long timeToKeepState) {
        this.timeToKeepState = timeToKeepState;
        return this;
    }

    public JaxRsClientBuilder threadSafe() {
        this.threadSafe = true;
        return this;
    }

    public JaxRsClientBuilder threadUnsafe() {
        this.threadSafe = false;
        return this;
    }

    public JaxRsClientBuilder setHeaders(Map<String, String> headers) {
        if (headers != null && !headers.isEmpty()) {
            this.headers.putAll(headers);
        }
        return this;
    }

    public JaxRsClientBuilder inheritHeaders() {
        this.inheritHeaders = true;
        return this;
    }

    public JaxRsClientBuilder disinheritHeaders() {
        this.inheritHeaders = false;
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> cls) {
        if (JAXRSClientFactoryBean.class.isAssignableFrom(cls)) {
            return (T) clientFactoryBean;
        }
        if (Client.class.isAssignableFrom(cls)) {
            if (target == null)
                throw new IllegalStateException("proxy not yet build");
            return (T) Proxy.getInvocationHandler(target);
        }
        throw new IllegalArgumentException("Unsupported unwrap target type [" + cls.getName() + "]");
    }

    public interface JaxRsClientFactoryConfig {

        void config(JAXRSClientFactoryBean factoryBean);

    }

}
