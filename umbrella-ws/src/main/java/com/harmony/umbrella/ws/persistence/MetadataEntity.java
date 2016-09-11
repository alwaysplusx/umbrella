package com.harmony.umbrella.ws.persistence;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.harmony.umbrella.data.domain.BaseEntity;
import com.harmony.umbrella.ws.Metadata;

/**
 * @author wuxii@foxmail.com
 */
@Entity
@Table(name = "UMBRELLA_WS_METADATA")
@NamedQueries({ 
    @NamedQuery(name = "MetadataEntity.findAll", query = "select o from MetadataEntity o"),
    @NamedQuery(name = "MetadataEntity.findAllServiceName", query = "select o.serviceName from MetadataEntity o"),
    @NamedQuery(name = "MetadataEntity.findByServiceName", query = "select o from MetadataEntity o where o.serviceName=:serviceName") 
})
public class MetadataEntity extends BaseEntity<String> implements Metadata, Serializable {

    private static final long serialVersionUID = 5573685617120186172L;
    /**
     * 服务的名称，类名
     */
    @Id
    protected String serviceName;

    /**
     * 服务地址，不允许为空
     */
    @Column(nullable = false)
    protected String address;
    
    protected String username;
    protected String password;
    protected long connectionTimeout = -1;
    protected long receiveTimeout = -1;
    protected int synchronousTimeout = -1;

    public MetadataEntity() {
    }

    public MetadataEntity(Class<?> serviceClass) {
        this.serviceName = serviceClass.getName();
    }

    public MetadataEntity(Class<?> serviceClass, String address) {
        this.serviceName = serviceClass.getName();
        this.address = address;
    }

    @Override
    public String getId() {
        return serviceName;
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
