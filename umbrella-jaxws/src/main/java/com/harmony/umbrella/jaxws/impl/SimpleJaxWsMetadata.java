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
package com.harmony.umbrella.jaxws.impl;

import com.harmony.umbrella.jaxws.JaxWsMetadata;

/**
 * 服务的基础元数据
 * 
 * @author wuxii@foxmail.com
 */
public class SimpleJaxWsMetadata implements JaxWsMetadata {

    private final Class<?> serviceClass;
    private String serviceName;
    private String address;
    private String username;
    private String password;

    public SimpleJaxWsMetadata(Class<?> serviceClass) {
        this.serviceClass = serviceClass;
        this.serviceName = serviceClass.getName();
    }

    public SimpleJaxWsMetadata(Class<?> serviceClass, String address) {
        this.serviceClass = serviceClass;
        this.address = address;
        this.serviceName = serviceClass.getName();
    }

    public SimpleJaxWsMetadata(Class<?> serviceClass, String address, String username, String password) {
        this.serviceClass = serviceClass;
        this.address = address;
        this.username = username;
        this.password = password;
        this.serviceName = serviceClass.getName();
    }

    @Override
    public String getServiceName() {
        return serviceName;
    }

    @Override
    public Class<?> getServiceClass() {
        return serviceClass;
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

}
