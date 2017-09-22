package com.harmony.umbrella.ws.jaxrs;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.jaxrs.client.Client;
import org.apache.cxf.jaxrs.client.JAXRSClientFactoryBean;
import org.apache.cxf.message.Message;
import org.springframework.util.Assert;

import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.ws.FactoryConfig;

/**
 * @author wuxii@foxmail.com
 */
public class JaxRsProxyBuilder {

    private static final Log log = Logs.getLog(JaxRsProxyBuilder.class);

    private final JAXRSClientFactoryBean clientFactoryBean = new JAXRSClientFactoryBean();

    private String address;
    private String username;
    private String password;

    private long timeToKeepState;

    private boolean inheritHeaders;

    private final Map<String, String> headers = new HashMap<String, String>();

    private boolean threadSafe = true;

    private Object target;

    public JaxRsProxyBuilder() {
    }

    public static JaxRsProxyBuilder create() {
        return new JaxRsProxyBuilder();
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

    public JaxRsProxyBuilder addInInterceptor(Interceptor<? extends Message> interceptor) {
        getInInterceptors().add(interceptor);
        return this;
    }

    public JaxRsProxyBuilder addOutInterceptor(Interceptor<? extends Message> interceptor) {
        getOutInterceptors().add(interceptor);
        return this;
    }

    public JaxRsProxyBuilder addInFaultInterceptor(Interceptor<? extends Message> interceptor) {
        getInFaultInterceptors().add(interceptor);
        return this;
    }

    public JaxRsProxyBuilder addOutFaultInterceptor(Interceptor<? extends Message> interceptor) {
        getOutFaultInterceptors().add(interceptor);
        return this;
    }

    public JaxRsProxyBuilder setAddress(String address) {
        this.address = address;
        return this;
    }

    public JaxRsProxyBuilder setUsername(String username) {
        this.username = username;
        return this;
    }

    public JaxRsProxyBuilder setPassword(String password) {
        this.password = password;
        return this;
    }

    public JaxRsProxyBuilder setSecondsToKeepState(long timeToKeepState) {
        this.timeToKeepState = timeToKeepState;
        return this;
    }

    public JaxRsProxyBuilder threadSafe() {
        this.threadSafe = true;
        return this;
    }

    public JaxRsProxyBuilder threadUnsafe() {
        this.threadSafe = false;
        return this;
    }

    public JaxRsProxyBuilder setHeaders(Map<String, String> headers) {
        if (headers != null && !headers.isEmpty()) {
            this.headers.putAll(headers);
        }
        return this;
    }

    public JaxRsProxyBuilder inheritHeaders() {
        this.inheritHeaders = true;
        return this;
    }

    public JaxRsProxyBuilder disinheritHeaders() {
        this.inheritHeaders = false;
        return this;
    }

    public <T> T build(Class<T> resourceClass) {
        return doBuild(resourceClass, null);
    }

    public <T> T build(Class<T> resourceClass, String address) {
        this.address = address;
        return doBuild(resourceClass, null);
    }

    public <T> T build(Class<T> resourceClass, long timeToKeepState) {
        this.timeToKeepState = timeToKeepState;
        return doBuild(resourceClass, null);
    }

    public <T> T build(Class<T> resourceClass, FactoryConfig<JAXRSClientFactoryBean> factoryConfig) {
        return doBuild(resourceClass, factoryConfig);
    }

    private <T> T doBuild(Class<T> resourceClass, FactoryConfig<JAXRSClientFactoryBean> factoryConfig) {
        Assert.notNull(resourceClass, "service class must be not null");
        Assert.hasLength(address, "proxy address is null or blank");

        if (factoryConfig != null) {
            factoryConfig.config(clientFactoryBean);
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

}
