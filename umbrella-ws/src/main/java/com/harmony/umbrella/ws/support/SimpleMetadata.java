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
package com.harmony.umbrella.ws.support;

import com.harmony.umbrella.ws.Metadata;

/**
 * 服务的基础元数据
 *
 * @author wuxii@foxmail.com
 */
public class SimpleMetadata implements Metadata {

    protected Class<?> serviceClass;
    protected String serviceName;
    protected String address;
    protected String username;
    protected String password;
    protected long connectionTimeout;
    protected long receiveTimeout;
    protected int synchronousTimeout;

    public SimpleMetadata(Class<?> serviceClass) {
        this(serviceClass, null);
    }

    public SimpleMetadata(Class<?> serviceClass, String address) {
        this(serviceClass, address, null, null);
    }

    public SimpleMetadata(Class<?> serviceClass, String address, String username, String password) {
        this(serviceClass, address, username, password, -1, -1, -1);
    }

    public SimpleMetadata(Class<?> serviceClass, String address, String username, String password, long ctime, long rtime, int stime) {
        this.serviceClass = serviceClass;
        this.serviceName = serviceClass == null ? null : serviceClass.getName();
        this.address = address;
        this.username = username;
        this.password = password;
        this.connectionTimeout = ctime;
        this.receiveTimeout = rtime;
        this.synchronousTimeout = stime;
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

    @Override
    public long getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(long connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    @Override
    public long getReceiveTimeout() {
        return receiveTimeout;
    }

    public void setReceiveTimeout(long receiveTimeout) {
        this.receiveTimeout = receiveTimeout;
    }

    @Override
    public int getSynchronousTimeout() {
        return synchronousTimeout;
    }

    public void setSynchronousTimeout(int synchronousTimeout) {
        this.synchronousTimeout = synchronousTimeout;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((address == null) ? 0 : address.hashCode());
        result = prime * result + ((password == null) ? 0 : password.hashCode());
        result = prime * result + ((serviceClass == null) ? 0 : serviceClass.hashCode());
        result = prime * result + ((username == null) ? 0 : username.hashCode());
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
        SimpleMetadata other = (SimpleMetadata) obj;
        if (address == null) {
            if (other.address != null)
                return false;
        } else if (!address.equals(other.address))
            return false;
        if (password == null) {
            if (other.password != null)
                return false;
        } else if (!password.equals(other.password))
            return false;
        if (serviceClass == null) {
            if (other.serviceClass != null)
                return false;
        } else if (!serviceClass.equals(other.serviceClass))
            return false;
        if (username == null) {
            if (other.username != null)
                return false;
        } else if (!username.equals(other.username))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("{\n")//
                .append("  serviceName  -> ").append(serviceName).append("\n")//
                .append("  serviceClass -> ").append(serviceClass != null ? "unknow" : serviceClass.getName()).append("\n")//
                .append("  address  -> ").append(address).append("\n")//
                .append("  password -> ").append(password).append("\n")//
                .append("  ctimeout -> ").append(connectionTimeout).append("\n")//
                .append("  rtimeout -> ").append(receiveTimeout).append("\n")//
                .append("  stimeout <-> ").append(synchronousTimeout).append("\n")//
                .append("\n}");
        return result.toString();
    }

}
