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

import java.util.List;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.frontend.ServerFactoryBean;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.message.Message;
import org.apache.cxf.service.invoker.Invoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.core.BeanFactory;
import com.harmony.umbrella.core.NoSuchBeanFindException;
import com.harmony.umbrella.core.SimpleBeanFactory;
import com.harmony.umbrella.jaxws.cxf.SimpleBeanFactoryInvoker;
import com.harmony.umbrella.util.Assert;

/**
 * 服务建造者 <p> 为方便使用,方法采用链式结构 <p> <em>one server one builder</em>
 * 
 * @author wuxii@foxmail.com
 */
public class JaxWsServerBuilder {

	private static final Logger log = LoggerFactory.getLogger(JaxWsServerBuilder.class);
	/**
	 * use {@link #getBeanFactory()} method
	 */
	private BeanFactoryInvoker beanFactoryInvoker;
	/**
	 * 服务的接口,可为空
	 */
	protected Class<?> serviceInterface;
	/**
	 * 服务的实现
	 */
	protected Class<?> serviceClass;
	/**
	 * 发布的地址
	 */
	protected String address;
	/**
	 * 访问服务的用户名
	 */
	protected String username;
	/**
	 * 访问服务的密码
	 */
	protected String password;
	/**
	 * 创建服务的服务工厂
	 */
	private JaxWsServerFactoryBean serverFactoryBean;

	/**
	 * 创建服务Builder
	 * 
	 * @return builder
	 */
	public static JaxWsServerBuilder newServerBuilder() {
		return new JaxWsServerBuilder();
	}

	/**
	 * 使用{@linkplain #setAddress(String)}, {@linkplain #setServiceClass(Class)}/
	 * {@linkplain #setServiceInterface(Class)}设置过的值来发布服务 <p>
	 * <b><em>发布时请确保值已经设置了必要的值</em>
	 * 
	 * @return jaxws服务
	 */
	public Server publish() {
		return publish(serviceClass, address, null);
	}

	/**
	 * 使用已经设置过地址{@linkplain #address}以及{@code serviceClass}发布服务 <p>
	 * {@code serviceClass}一般为服务的实现类, 如果设置的
	 * {@linkplain #setBeanFactory(BeanFactory)}支持通过接口获取bean. 那么将直接使用
	 * {@linkplain BeanFactory#getBean(Class)}的bean来作为服务实例
	 * 
	 * @param serviceClass
	 *            服务类.
	 * @return jaxws服务
	 */
	public Server publish(Class<?> serviceClass) {
		return publish(serviceClass, address, null);
	}

	/**
	 * 使用{@code serviceClass}以及{@code address}来发布服务 <p> {@code serviceClass}
	 * 一般为服务的实现类, 如果设置的 {@linkplain #setBeanFactory(BeanFactory)}支持通过接口获取bean.
	 * 那么将直接使用 {@linkplain BeanFactory#getBean(Class)}的bean来作为服务实例
	 * 
	 * @param serviceClass
	 *            服务类
	 * @param address
	 *            发布地址
	 * @return jaxws服务
	 */
	public Server publish(Class<?> serviceClass, String address) {
		return publish(serviceClass, address, null);
	}

	/**
	 * 使用{@code serviceClass}, {@code address}和{@code factoryConfig}发布服务 <p>
	 * {@code serviceClass}一般为服务的实现类, 如果设置的
	 * {@linkplain #setBeanFactory(BeanFactory)}支持通过接口获取bean. 那么将直接使用
	 * {@linkplain BeanFactory#getBean(Class)}的bean来作为服务实例 <p>
	 * {@code factoryConfig}提供创建前的配置回调,可以在{@link #serverFactoryBean} 创建前
	 * 设置其他感兴趣的属性. 但是不可以在{@code factoryConfig}中调用
	 * {@linkplain ServerFactoryBean#create()}方法
	 * 
	 * @param serviceClass
	 *            服务类
	 * @param address
	 *            发布的地址
	 * @param factoryConfig
	 *            工厂配置信息
	 * @return jaxws服务
	 * @throws IllegalStateException
	 *             在配置{@code factoryConfig}中调用了{@link #serverFactoryBean}
	 *             的create方法
	 * @see JaxWsServerBuilder#doPublish(Class, String,
	 *      JaxWsServerFactoryConfig)
	 */
	public Server publish(Class<?> serviceClass, String address, JaxWsServerFactoryConfig factoryConfig) {
		this.serviceClass = serviceClass;
		this.address = address;
		return doPublish(serviceClass, address, factoryConfig);
	}

	/**
	 * 创建并发布服务
	 * 
	 * @param serviceClass
	 *            服务类
	 * @param address
	 *            服务发布的地址
	 * @param factoryConfig
	 *            工厂配置信息
	 * @return jaxws服务
	 * @throws IllegalArgumentException
	 *             serviceClass or address is null
	 * @throws IllegalStateException
	 *             factoryConfig中调用了serverFactoryBean的create方法
	 */
	protected Server doPublish(Class<?> serviceClass, String address, JaxWsServerFactoryConfig factoryConfig) {
		Assert.notNull(address, "publish address is null");
		Assert.notNull(serviceClass, "service class is null");
		try {
			serverFactoryBean = new JaxWsServerFactoryBean();
			if (factoryConfig != null) {
				factoryConfig.config(serverFactoryBean);
				if (serverFactoryBean.getServer() != null) {
					throw new IllegalStateException("config factory not allow call create method");
				}
			}
			if (log.isInfoEnabled()) {
				addInOutLogging(serverFactoryBean);
			}
			applyPrefectServiceClass(serverFactoryBean, serviceClass, serviceInterface);
			serverFactoryBean.setInvoker(getBeanFactoryInvoker());
			serverFactoryBean.setAddress(address);
			return serverFactoryBean.create();
		} catch (NoSuchBeanFindException e) {
			throw new JaxWsException("no target bean of class " + serviceClass.getName() + " find");
		}
	}

	protected void applyPrefectServiceClass(JaxWsServerFactoryBean factoryBean, Class<?> serviceClass, Class<?> serviceInterface) {
		factoryBean.setServiceClass(serviceClass);
	}

	/**
	 * 如果{@linkplain factoryBean}中为添加in,out日志则添加日志拦截器
	 * 
	 * @param factoryBean
	 *            服务工厂
	 */
	protected void addInOutLogging(JaxWsServerFactoryBean factoryBean) {
		List<Interceptor<? extends Message>> inInterceptors = factoryBean.getInInterceptors();
		int i, max;
		for (i = 0, max = inInterceptors.size(); i < max; i++) {
			if (inInterceptors.get(i) instanceof LoggingInInterceptor) {
				break;
			}
		}
		if (i == max) {
			inInterceptors.add(new LoggingInInterceptor());
		}
		List<Interceptor<? extends Message>> outInterceptors = factoryBean.getOutInterceptors();
		for (i = 0, max = outInterceptors.size(); i < max; i++) {
			if (outInterceptors.get(i) instanceof LoggingOutInterceptor) {
				break;
			}
		}
		if (i == max) {
			outInterceptors.add(new LoggingOutInterceptor());
		}
	}

	/**
	 * 懒初始化 <p> 当beanFactory为空时候才使用默认的{@linkplain SimpleBeanFactory}
	 */
	protected BeanFactoryInvoker getBeanFactoryInvoker() {
		if (beanFactoryInvoker == null) {
			beanFactoryInvoker = new SimpleBeanFactoryInvoker(serviceClass);
		}
		return beanFactoryInvoker;
	}

	/**
	 * 设置bean工厂
	 * 
	 * @param beanFactory
	 *            bean工厂
	 * @return current builder
	 */
	public JaxWsServerBuilder setBeanFactoryInvoker(BeanFactoryInvoker beanFactoryInvoker) {
		this.beanFactoryInvoker = beanFactoryInvoker;
		return this;
	}

	/**
	 * 设置服务接口
	 * 
	 * @param serviceInterface
	 *            服务接口
	 * @return current builder
	 */
	public JaxWsServerBuilder setServiceInterface(Class<?> serviceInterface) {
		this.serviceInterface = serviceInterface;
		return this;
	}

	/**
	 * 设置服务实现类
	 * 
	 * @param serviceClass
	 *            服务实现类
	 * @return current builder
	 */
	public JaxWsServerBuilder setServiceClass(Class<?> serviceClass) {
		this.serviceClass = serviceClass;
		return this;
	}

	/**
	 * 设置服务地址
	 * 
	 * @param address
	 *            服务地址
	 * @return current builder
	 */
	public JaxWsServerBuilder setAddress(String address) {
		this.address = address;
		return this;
	}

	/**
	 * 设置服务用户名
	 * 
	 * @param username
	 *            用户名
	 * @return current builder
	 */
	public JaxWsServerBuilder setUsername(String username) {
		this.username = username;
		return this;
	}

	/**
	 * 设置服务密码
	 * 
	 * @param password
	 *            用户密码
	 * @return current builder
	 */
	public JaxWsServerBuilder setPassword(String password) {
		this.password = password;
		return this;
	}

	/**
	 * 获取builder中的信息
	 * 
	 * @param cls
	 *            待获取的类
	 * @return 指定的类信息
	 * @throws IllegalArgumentException
	 *             不支持的类型
	 */
	@SuppressWarnings("unchecked")
	public <T> T unwrap(Class<T> cls) {
		if (ServerFactoryBean.class.isAssignableFrom(cls)) {
			return (T) serverFactoryBean;
		}
		throw new IllegalArgumentException("Unsupported unwrap target type [" + cls.getName() + "]");
	}

	/**
	 * 服务创建前的回调配置
	 * 
	 * @author wuxii@foxmail.com
	 */
	public interface JaxWsServerFactoryConfig {

		void config(JaxWsServerFactoryBean factoryBean);

	}

	public interface BeanFactoryInvoker extends Invoker, BeanFactory {

	}

}
