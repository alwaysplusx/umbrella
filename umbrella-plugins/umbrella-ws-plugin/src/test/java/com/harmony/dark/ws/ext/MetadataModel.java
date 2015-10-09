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
package com.harmony.dark.ws.ext;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.harmony.umbrella.data.domain.Model;
import com.harmony.umbrella.ws.Metadata;

/**
 * @author wuxii@foxmail.com
 */
@Entity
@Table(name = "UMBRELLA_WS_METADATA")
public class MetadataModel extends Model<String> implements Metadata {

    private static final long serialVersionUID = 5573685617120186172L;
    /**
     * 服务的名称，类名
     */
    @Id
    @Column(name = "serviceName")
    protected String serviceName;

    /**
     * 服务地址，不允许为空
     */
    @Column(name = "address", nullable = false)
    protected String address;

    @Column(name = "username")
    protected String username;

    @Column(name = "password")
    protected String password;

    @Column(name = "connectionTimeout")
    protected long connectionTimeout = -1;

    @Column(name = "receiveTimeout")
    protected long receiveTimeout = -1;

    @Column(name = "synchronousTimeout")
    protected int synchronousTimeout = -1;

    public MetadataModel() {
    }

    public MetadataModel(Class<?> serviceClass) {
        this.serviceName = serviceClass.getName();
    }

    public MetadataModel(Class<?> serviceClass, String address) {
        this.serviceName = serviceClass.getName();
        this.address = address;
    }

    @Override
    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(Class<?> serviceClass) {
        this.serviceName = serviceClass == null ? null : serviceClass.getName();
    }

    @Override
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
    public Class<?> getServiceClass() {
        try {
            return serviceName == null ? null : Class.forName(serviceName);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{serviceName:");
        builder.append(serviceName);
        builder.append(", address:");
        builder.append(address);
        builder.append(", username:");
        builder.append(username);
        builder.append(", password:");
        builder.append(password);
        builder.append(", connectionTimeout:");
        builder.append(connectionTimeout);
        builder.append(", receiveTimeout:");
        builder.append(receiveTimeout);
        builder.append(", synchronousTimeout:");
        builder.append(synchronousTimeout);
        builder.append("}");
        return builder.toString();
    }

}
