package com.harmony.umbrella.ws.jaxws;

import java.lang.reflect.Proxy;
import java.util.List;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.ClientImpl;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.frontend.ClientProxyFactoryBean;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.message.Message;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.springframework.util.Assert;

import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.ws.FactoryConfig;

/**
 * Proxy Builder 创建代理实例
 * 
 * @author wuxii@foxmail.com
 */
public class JaxWsProxyBuilder {

    public static final long DEFAULT_TIMEOUT = 1000 * 60 * 3;

    // JaxWsProxyFactoryBean's reflectionServiceFactory#serviceConfigurations
    // add configuration all the time when call create method
    private final JaxWsProxyFactoryBean factoryBean = new JaxWsProxyFactoryBean();

    private static final Log log = Logs.getLog(JaxWsProxyBuilder.class);

    private String address;
    private String username;
    private String password;

    /**
     * 实际的代理服务类
     */
    private Object target;

    private long receiveTimeout = -1;
    private long connectionTimeout = -1;
    private int synchronousTimeout = -1;

    public static JaxWsProxyBuilder create() {
        return new JaxWsProxyBuilder();
    }

    /**
     * @see JaxWsProxyFactoryBean#getInInterceptors()
     */
    public List<Interceptor<? extends Message>> getInInterceptors() {
        return factoryBean.getInInterceptors();
    }

    /**
     * @see JaxWsProxyFactoryBean#getInFaultInterceptors()
     */
    public List<Interceptor<? extends Message>> getInFaultInterceptors() {
        return factoryBean.getInFaultInterceptors();
    }

    /**
     * @see JaxWsProxyFactoryBean#getOutFaultInterceptors()
     */
    public List<Interceptor<? extends Message>> getOutFaultInterceptors() {
        return factoryBean.getOutFaultInterceptors();
    }

    /**
     * @see JaxWsProxyFactoryBean#getOutInterceptors()
     */
    public List<Interceptor<? extends Message>> getOutInterceptors() {
        return factoryBean.getOutInterceptors();
    }

    public JaxWsProxyBuilder addInInterceptor(Interceptor<? extends Message> interceptor) {
        getInInterceptors().add(interceptor);
        return this;
    }

    public JaxWsProxyBuilder addOutInterceptor(Interceptor<? extends Message> interceptor) {
        getOutInterceptors().add(interceptor);
        return this;
    }

    public JaxWsProxyBuilder addInFaultInterceptor(Interceptor<? extends Message> interceptor) {
        getInFaultInterceptors().add(interceptor);
        return this;
    }

    public JaxWsProxyBuilder addOutFaultInterceptor(Interceptor<? extends Message> interceptor) {
        getOutFaultInterceptors().add(interceptor);
        return this;
    }

    /**
     * 设置代理服务的地址
     * 
     * @param address
     *            服务地址
     */
    public JaxWsProxyBuilder setAddress(String address) {
        this.address = address;
        return this;
    }

    /**
     * 设置代理服务的用户密码
     * 
     * @param username
     *            用户名
     */
    public JaxWsProxyBuilder setUsername(String username) {
        this.username = username;
        return this;
    }

    /**
     * 设置代理服务的密码
     * 
     * @param password
     *            用户密码
     */
    public JaxWsProxyBuilder setPassword(String password) {
        this.password = password;
        return this;
    }

    /**
     * 设置接收超时时间，单位毫秒
     * 
     * @param receiveTimeout
     *            接收超时时间
     */
    public JaxWsProxyBuilder setReceiveTimeout(long receiveTimeout) {
        this.receiveTimeout = receiveTimeout;
        return this;
    }

    /**
     * 设置连接超时时间，单位毫秒
     * 
     * @param connectionTimeout
     *            连接超时时间
     * @see org.apache.cxf.transport.http.HTTPConduit
     */
    public JaxWsProxyBuilder setConnectionTimeout(long connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        return this;
    }

    /**
     * 设置{@linkplain ClientImpl#setSynchronousTimeout(int)}
     * 
     * @param synchronousTimeout
     *            线程同步等待时间
     */
    public JaxWsProxyBuilder setSynchronousTimeout(int synchronousTimeout) {
        this.synchronousTimeout = synchronousTimeout;
        return this;
    }

    /**
     * 创建代理服务
     * 
     * @param serviceClass
     *            代理服务类型
     * @return 代理服务
     */
    public <T> T build(Class<T> serviceClass) {
        return doBuild(serviceClass, null);
    }

    /**
     * 创建代理服务. 并连接到指定的服务地址
     * 
     * @param serviceClass
     *            代理服务类型
     * @param address
     *            代理服务连接的地址
     * @return 代理服务
     */
    public <T> T build(Class<T> serviceClass, String address) {
        this.address = address;
        return doBuild(serviceClass, null);
    }

    /**
     * 创建代理服务. 并设置超时时间
     * 
     * @param serviceClass
     *            代理服务类型
     * @param connectionTimeout
     *            连接超时时间
     */
    public <T> T build(Class<T> serviceClass, long connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        return doBuild(serviceClass, null);
    }

    /**
     * 创建代理服务，并创建前提供{@linkplain FactoryConfig}配置工厂属性
     * 
     * @param serviceClass
     *            代理服务类型
     * @param factoryConfig
     *            服务配置项
     */
    public <T> T build(Class<T> serviceClass, FactoryConfig<JaxWsProxyFactoryBean> factoryConfig) {
        return doBuild(serviceClass, factoryConfig);
    }

    private <T> T doBuild(Class<T> serviceClass, FactoryConfig<JaxWsProxyFactoryBean> factoryConfig) {
        Assert.notNull(serviceClass, "service class must be not null");
        Assert.hasLength(address, "proxy address is null or blank");

        if (factoryConfig != null) {
            factoryConfig.config(factoryBean);
        }

        factoryBean.setAddress(address);
        factoryBean.setUsername(username);
        factoryBean.setPassword(password);
        factoryBean.setServiceClass(serviceClass);

        target = factoryBean.create(serviceClass);

        // 设置最长超时时间按
        if (connectionTimeout > 0) {
            setConnectionTimeout(target, connectionTimeout);
        }

        // 设置最长超时时间
        if (receiveTimeout > 0) {
            setReceiveTimeout(target, receiveTimeout);
        }

        // 设置返回等待时间
        if (synchronousTimeout > 0) {
            setSynchronousTimeout(target, synchronousTimeout);
        }

        log.debug("build proxy[{}] successfully", serviceClass.getName());
        return serviceClass.cast(target);
    }

    /**
     * 获取代理工厂中的内容
     * 
     * @param cls
     *            期待的类型
     * @return
     */
    public <T> T unwrap(Class<T> cls) {
        if (ClientProxyFactoryBean.class.isAssignableFrom(cls)) {
            return (T) factoryBean;
        }
        if (ClientProxy.class.isAssignableFrom(cls)) {
            if (target == null) {
                throw new IllegalStateException("proxy not yet build");
            }
            return (T) Proxy.getInvocationHandler(target);
        }
        if (Client.class.isAssignableFrom(cls)) {
            if (target == null) {
                throw new IllegalStateException("proxy not yet build");
            }
            return (T) ClientProxy.getClient(target);
        }
        throw new IllegalArgumentException("Unsupported unwrap target type [" + cls.getName() + "]");
    }

    /**
     * 设置Http接收超时时间
     * 
     * @param target
     *            待设置的代理对象
     * @param receiveTimeout
     *            接收等待时间
     * @see org.apache.cxf.transports.http.configuration.HTTPClientPolicy#setReceiveTimeout(long)
     *      HTTPClientPolicy.setReceiveTimeout(long)
     */
    public static void setReceiveTimeout(Object target, long receiveTimeout) {
        if (receiveTimeout < 0) {
            return;
        }
        Client proxy = ClientProxy.getClient(target);
        HTTPConduit conduit = (HTTPConduit) proxy.getConduit();
        HTTPClientPolicy policy = new HTTPClientPolicy();
        policy.setReceiveTimeout(receiveTimeout);
        conduit.setClient(policy);
    }

    /**
     * 设置Http连接超时时间
     * 
     * @param target
     *            待设置的代理对象
     * @param connectionTimeout
     *            连接等待时间
     * @see org.apache.cxf.transports.http.configuration.HTTPClientPolicy#setConnectionTimeout(long)
     */
    public static void setConnectionTimeout(Object target, long connectionTimeout) {
        if (connectionTimeout < 0) {
            return;
        }
        Client proxy = ClientProxy.getClient(target);
        HTTPConduit conduit = (HTTPConduit) proxy.getConduit();
        HTTPClientPolicy policy = new HTTPClientPolicy();
        policy.setConnectionTimeout(connectionTimeout);
        conduit.setClient(policy);
    }

    /**
     * 设置Client的同步等待时间
     * 
     * @param target
     *            待设置的代理对象
     * @param synchronousTimeout
     *            同步等待时间
     * @see ClientImpl#setSynchronousTimeout(int)
     */
    public static void setSynchronousTimeout(Object target, int synchronousTimeout) {
        if (synchronousTimeout < 0) {
            return;
        }
        Client client = ClientProxy.getClient(target);
        if (client instanceof ClientImpl) {
            ClientImpl clientImpl = (ClientImpl) client;
            clientImpl.setSynchronousTimeout(synchronousTimeout);
        }
    }

}
