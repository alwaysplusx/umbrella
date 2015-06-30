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
import java.util.Map;

import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.apache.cxf.message.Message;

/**
 * @author wuxii@foxmail.com
 */
@SuppressWarnings("unused")
public class JaxRsClientBuilder {

    // TODO

    private final JAXRSClientFactoryBean clientFactoryBean;

    private String address;
    private String username;
    private String password;

    private long timeToKeepState;

    private boolean inheritHeaders;

    private Map<String, String> headers;

    private boolean threadSafe = true;

    public JaxRsClientBuilder() {
        this.clientFactoryBean = new JAXRSClientFactoryBean();
    }

    public <T> T build(Class<T> resourceClass) {
        return null;
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
        this.headers = headers;
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

}
