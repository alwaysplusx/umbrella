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
package com.harmony.umbrella.jaxws;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.jaxws.cxf.interceptor.MessageInInterceptor;
import com.harmony.umbrella.jaxws.cxf.interceptor.MessageOutInterceptor;
import com.harmony.umbrella.jaxws.impl.SimpleJaxWsMetadata;
import com.harmony.umbrella.util.Assert;
import com.harmony.umbrella.util.StringUtils;

/**
 * Proxy Builder 创建代理实例
 * 
 * @author wuxii@foxmail.com
 */
public class JaxWsProxyBuilder {

	public static final long defaultTimeout = 1000 * 60 * 3;

	private static final ThreadLocal<JaxWsProxyFactoryBean> factoryBeans = new ThreadLocal<JaxWsProxyFactoryBean>();

	private static final Logger log = LoggerFactory.getLogger(JaxWsProxyBuilder.class);

	private String address;
	private String username;
	private String password;

	private JaxWsMetadataLoader metaLoader;

	/**
	 * 实际的代理服务类
	 */
	private Object target;

	private long receiveTimeout = -1;
	private long connectionTimeout = -1;
	private int synchronousTimeout = -1;

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
		factoryBean.getInInterceptors().add(new MessageInInterceptor());
		factoryBean.getOutInterceptors().add(new MessageOutInterceptor());
	}

	/**
	 * @see JaxWsProxyFactoryBean#getInInterceptors()
	 */
	public List<Interceptor<? extends Message>> getInInterceptors() {
		return factoryBeans.get().getInInterceptors();
	}

	/**
	 * @see JaxWsProxyFactoryBean#getInFaultInterceptors()
	 */
	public List<Interceptor<? extends Message>> getInFaultInterceptors() {
		return factoryBeans.get().getInFaultInterceptors();
	}

	/**
	 * @see JaxWsProxyFactoryBean#getOutFaultInterceptors()
	 */
	public List<Interceptor<? extends Message>> getOutFaultInterceptors() {
		return factoryBeans.get().getOutFaultInterceptors();
	}

	/**
	 * @see JaxWsProxyFactoryBean#getOutInterceptors()
	 */
	public List<Interceptor<? extends Message>> getOutInterceptors() {
		return factoryBeans.get().getOutInterceptors();
	}

	/**
	 * 设置代理服务的地址，该操作发生在{@link #setJaxWsMetadataLoader(JaxWsMetadataLoader)}
	 * 之后则不在加载该属性
	 * 
	 * @param address
	 * @return
	 */
	public JaxWsProxyBuilder setAddress(String address) {
		this.address = address;
		return this;
	}

	/**
	 * 设置代理服务的用户密码，该操作发生在{@link #setJaxWsMetadataLoader(JaxWsMetadataLoader)}
	 * 之后则不在加载该属性
	 * 
	 * @param username
	 * @return
	 */
	public JaxWsProxyBuilder setUsername(String username) {
		this.username = username;
		return this;
	}

	/**
	 * 设置代理服务的密码，该操作发生在{@link #setJaxWsMetadataLoader(JaxWsMetadataLoader)}
	 * 之后则不在加载该属性
	 * 
	 * @param password
	 * @return
	 */
	public JaxWsProxyBuilder setPassword(String password) {
		this.password = password;
		return this;
	}

	/**
	 * 设置接收超时时间，单位毫秒
	 * 
	 * @param receiveTimeout
	 * @return
	 */
	public JaxWsProxyBuilder setReceiveTimeout(long receiveTimeout) {
		this.receiveTimeout = receiveTimeout;
		return this;
	}

	/**
	 * 设置连接超时时间，单位毫秒
	 * 
	 * @param connectionTimeout
	 * @return
	 * @see HTTPConduit#set
	 */
	public JaxWsProxyBuilder setConnectionTimeout(long connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
		return this;
	}

    /**
     * 设置{@linkplain ClientImpl#setSynchronousTimeout(int)}
     * 
     * @param synchronousTimeout
     * @return
     */
    public JaxWsProxyBuilder setSynchronousTimeout(int synchronousTimeout) {
        this.synchronousTimeout = synchronousTimeout;
        return this;
    }
	
	/**
	 * 创建代理服务，并创建前提供{@linkplain JaxWsProxyFactoryConfig}配置工厂属性
	 * 
	 * @param serviceClass
	 * @param proxyConfig
	 * @return
	 */
	public <T> T build(Class<T> serviceClass, JaxWsProxyFactoryConfig proxyConfig) {
	    Assert.notNull(serviceClass, "service class must be not null");
		JaxWsMetadata metadata = getJaxWsMetadata(serviceClass);
		Assert.isTrue(StringUtils.isNotBlank(metadata.getAddress()), "proxy address is null or blank");
		
		JaxWsProxyFactoryBean factoryBean = factoryBeans.get();
		if (proxyConfig != null)
			proxyConfig.config(factoryBean);
		
		factoryBean.setAddress(metadata.getAddress());
		factoryBean.setUsername(metadata.getUsername());
		factoryBean.setPassword(metadata.getPassword());
		
		target = factoryBean.create(serviceClass);
		
		// 设置最长超时时间按
		long connTimeout = metadata.getConnectionTimeout();
		if (connTimeout > 0) {
			setConnectionTimeout(target, connTimeout);
		}
		
		// 设置最长超时时间
		long recTimeout = metadata.getReceiveTimeout();
		if (recTimeout > 0) {
			setReceiveTimeout(target, recTimeout);
		}
		
		// 设置返回等待时间
		int syncTimeout = metadata.getSynchronousTimeout();
        if (synchronousTimeout > 0) {
            setSynchronousTimeout(target, syncTimeout);
        }
		
		log.debug("build proxy[{}] successfully", serviceClass.getName());
		return serviceClass.cast(target);
	}

	/**
	 * 如果{@linkplain #metaLoader}不为空则先从loader中加载元数据.
	 * <p>
	 * 另,如果当前 {@linkplain JaxWsProxyBuilder}中:
	 * <li>{@linkplain #address}不为空则覆盖元数据中的地址
	 * <li>{@linkplain #username}不为空则覆盖元数据中的用户名
	 * <li>{@linkplain #password} 不为空则覆盖元数据中的密码
	 * 
	 * @param serviceClass
	 * @return
	 */
    protected JaxWsMetadata getJaxWsMetadata(Class<?> serviceClass) {
        SimpleJaxWsMetadata result = new SimpleJaxWsMetadata(serviceClass);
        if (metaLoader == null) {
            result.setAddress(address);
            result.setUsername(username);
            result.setPassword(password);
            result.setConnectionTimeout(connectionTimeout);
            result.setReceiveTimeout(receiveTimeout);
            result.setSynchronousTimeout(synchronousTimeout);
        } else {
            JaxWsMetadata temp = metaLoader.getJaxWsMetadata(serviceClass);
            result.setAddress(StringUtils.isBlank(address) ? temp.getAddress() : address);
            result.setUsername(StringUtils.isBlank(username) ? temp.getUsername() : username);
            result.setPassword(StringUtils.isBlank(password) ? temp.getPassword() : password);
            result.setServiceName(temp.getServiceName());
            result.setConnectionTimeout(connectionTimeout > temp.getConnectionTimeout() ? connectionTimeout : temp.getConnectionTimeout());
            result.setReceiveTimeout(receiveTimeout > temp.getReceiveTimeout() ? receiveTimeout : temp.getReceiveTimeout());
            result.setSynchronousTimeout(synchronousTimeout > temp.getSynchronousTimeout() ? synchronousTimeout : temp.getSynchronousTimeout());
        }
        return result;
    }

	/**
	 * 创建代理服务
	 * 
	 * @param serviceClass
	 * @return
	 */
	public <T> T build(Class<T> serviceClass) {
		return build(serviceClass, (JaxWsProxyFactoryConfig) null);
	}

	/**
	 * 创建代理服务. 并连接到指定的服务地址
	 * 
	 * @param serviceClass
	 * @param address
	 * @return
	 */
	public <T> T build(Class<T> serviceClass, String address) {
		this.address = address;
		return build(serviceClass, (JaxWsProxyFactoryConfig) null);
	}

	/**
	 * 创建代理服务, 并使用指定的元数据加载工具加载元数据信息(地址/用户名/密码).
	 * 
	 * @param serviceClass
	 * @param metaLoader
	 * @return
	 */
	public <T> T build(Class<T> serviceClass, JaxWsMetadataLoader metaLoader) {
		this.metaLoader = metaLoader;
		return build(serviceClass, (JaxWsProxyFactoryConfig) null);
	}

	/**
	 * 创建代理服务. 并设置超时时间
	 * 
	 * @param serviceClass
	 * @param connectionTimeout
	 * @return
	 */
	public <T> T build(Class<T> serviceClass, long connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
		T target = build(serviceClass);
		return target;
	}

	/**
	 * 设置元数据加载工具
	 * 
	 * @param loader
	 * @return
	 */
	public JaxWsProxyBuilder setJaxWsMetadataLoader(JaxWsMetadataLoader loader) {
		this.metaLoader = loader;
		return this;
	}

    /**
     * 获取代理工厂中的内容
     * 
     * @param cls
     *            期待的类型
     * @return
     */
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

    /**
     * 设置Http接收超时时间
     * 
     * @param target
     * @param receiveTimeout
     * @see HTTPClientPolicy#setReceiveTimeout(long)
     */
    public static void setReceiveTimeout(Object target, long receiveTimeout) {
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
     * @param connectionTimeout
     * @see HTTPClientPolicy#setConnectionTimeout(long)
     */
    public static void setConnectionTimeout(Object target, long connectionTimeout) {
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
     * @param synchronousTimeout
     * @see ClientImpl#setSynchronousTimeout(int)
     */
    public static void setSynchronousTimeout(Object target, int synchronousTimeout) {
        Client client = ClientProxy.getClient(target);
        if (client instanceof ClientImpl) {
            ClientImpl clientImpl = (ClientImpl) client;
            clientImpl.setSynchronousTimeout(synchronousTimeout);
        }
    }
	
	/**
	 * 创建时候的回调方法，负责创建前配置{@linkplain JaxWsProxyFactoryBean}
	 * <p>
	 * 不允许在 {@link #config(JaxWsProxyFactoryBean)}中调用
	 * {@linkplain JaxWsProxyFactoryBean#create()}方法
	 */
	public interface JaxWsProxyFactoryConfig {

		void config(JaxWsProxyFactoryBean factoryBean);

	}

}
