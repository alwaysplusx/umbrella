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

import static com.harmony.umbrella.jaxws.JaxWsServerBuilder.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.cxf.frontend.ServerFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.jaxws.JaxWsServerBuilder.BeanFactoryInvoker;
import com.harmony.umbrella.jaxws.JaxWsServerBuilder.JaxWsServerFactoryConfig;
import com.harmony.umbrella.util.Assert;
import com.harmony.umbrella.util.Exceptions;

/**
 * 基于cxf的WebService服务管理
 * 
 * @author wuxii@foxmail.com
 */
public class JaxWsServerManager {

	private static final Logger log = LoggerFactory.getLogger(JaxWsServerManager.class);
	/**
	 * 一个服务serviceClass对应一个服务信息
	 */
	private static final Map<Class<?>, JaxWsServer> servers = new HashMap<Class<?>, JaxWsServer>();

	/**
	 * 管理实例
	 */
	private static JaxWsServerManager instance;

	/**
	 * 服务元数据加载器
	 */
	private MetadataLoader metadataLoader;

	/**
	 * @see org.apache.cxf.service.invoker.Invoker
	 */
	private BeanFactoryInvoker beanFactoryInvoker;

	/**
	 * 当遇到服务发布失败，则结束剩余的服务发布
	 */
	private boolean failFast = false;

	private JaxWsServerManager() {
	}

	public static final JaxWsServerManager getInstance() {
		if (instance == null) {
			synchronized (JaxWsServerManager.class) {
				if (instance == null) {
					instance = new JaxWsServerManager();
				}
			}
		}
		return instance;
	}

	/**
	 * 发布一个服务实例为{@code serviceClass}的服务，地址依赖{@link #metadataLoader}
	 * 来加载。所以在使用这个方法时候请注意需要先设置{@link #metadataLoader}
	 * 
	 * @param serviceClass
	 *            服务实例类型
	 */
	public boolean publish(Class<?> serviceClass) {
		return publish(serviceClass, metadataLoader.getAddress(serviceClass), null);
	}

	/**
	 * 在指定地址发布一个服务
	 * 
	 * @param serviceClass
	 *            服务类型
	 * @param address
	 *            服务地址
	 */
	public boolean publish(Class<?> serviceClass, String address) {
		return publish(serviceClass, address, null);
	}

	/**
	 * 可配置的发布服务
	 * 
	 * @param serviceClass
	 *            服务类型
	 * @param factoryConfig
	 *            服务配置回调
	 */
	public boolean publish(Class<?> serviceClass, JaxWsServerFactoryConfig factoryConfig) {
		return publish(serviceClass, metadataLoader.getAddress(serviceClass), factoryConfig);
	}

	/**
	 * 在指定地址发布服务，发布前提供配置服务
	 * 
	 * @param serviceClass
	 *            服务类型
	 * @param address
	 *            服务地址
	 * @param factoryConfig
	 *            服务配置回调
	 */
	public boolean publish(Class<?> serviceClass, String address, JaxWsServerFactoryConfig factoryConfig) {
		Assert.notNull(serviceClass, "service class is null");
		Assert.notNull(address, "address is null");
		return doPublish(serviceClass, address, factoryConfig);
	}

	private boolean doPublish(Class<?> serviceClass, String address, JaxWsServerFactoryConfig factoryConfig) {
		JaxWsServerBuilder builder;
		try {
			builder = newServerBuilder();
			if (beanFactoryInvoker != null) {
				builder.setBeanFactoryInvoker(beanFactoryInvoker);
			}
			if (metadataLoader != null) {
				builder.setUsername(metadataLoader.getUsername(serviceClass));
				builder.setPassword(metadataLoader.getPassword(serviceClass));
			}
			builder.publish(serviceClass, address, factoryConfig);
			registerServerInstance(builder);
			log.info("success publish server[serviceClass:{},address:{}]", serviceClass.getName(), address);
		} catch (Exception e) {
			log.error("failed publish server[serviceClass:{},address:{}]", serviceClass.getName(), address, e);
			if (failFast) {
				throw Exceptions.unchecked(e);
			}
			return false;
		}
		return true;
	}

	/**
	 * 设置服务的元数据加载器
	 */
	public void setMetadataLoader(MetadataLoader metadataLoader) {
		this.metadataLoader = metadataLoader;
	}

	/**
	 * @see org.apache.cxf.service.invoker.Invoker
	 */
	public void setBeanFactoryInvoker(BeanFactoryInvoker beanFactoryInvoker) {
		this.beanFactoryInvoker = beanFactoryInvoker;
	}

	/**
	 * 设置快速失败策略
	 */
	public void setFailFast(boolean failFast) {
		this.failFast = failFast;
	}

	/**
	 * 检测地址是否已经发布了服务
	 * 
	 * @param address
	 * @return
	 */
	public boolean inUse(String address) {
		for (JaxWsServer server : servers.values()) {
			if (server.inUse(address))
				return true;
		}
		return false;
	}

	/**
	 * 检查是否在地址上发布了指定类型的服务
	 * 
	 * @param serviceClass
	 * @param address
	 * @return
	 */
	public boolean isPublished(Class<?> serviceClass, String address) {
		if (servers.containsKey(serviceClass)) {
			return servers.get(serviceClass).inUse(address);
		}
		return false;
	}

	private void registerServerInstance(JaxWsServerBuilder builder) {
		ServerFactoryBean factoryBean = builder.unwrap(ServerFactoryBean.class);
		Class<?> serviceClass = factoryBean.getServiceClass();
		JaxWsServer server = servers.get(serviceClass);
		if (server != null) {
			server = new JaxWsServer(serviceClass);
			servers.put(serviceClass, server);
		}
		server.addServiceInstance(factoryBean.getAddress(), factoryBean);
	}

	/**
	 * 销毁指定地址的服务实例
	 * 
	 * @param serviceClass
	 * @param address
	 */
	public void destory(String address) {
		for (JaxWsServer server : servers.values()) {
			server.removeInstance(address);
			if (!server.hasServiceInstance()) {
				servers.remove(server.getServiceClass());
			}
		}
	}

	/**
	 * 销毁所有serviceClass的服务实例
	 * 
	 * @param serviceClass
	 */
	public void destory(Class<?> serviceClass) {
		JaxWsServer server = servers.get(serviceClass);
		for (String address : server.listAddress()) {
			server.removeInstance(address);
		}
		servers.remove(serviceClass);
	}

	/**
	 * 销毁所有服务实例
	 */
	public void destoryAll() {
		for (Class<?> serviceClass : servers.keySet()) {
			destory(serviceClass);
		}
	}

	/**
	 * 单个服务的所有信息. <p> 服务可以发布在多个地址
	 * 
	 * @author wuxii@foxmail.com
	 */
	private class JaxWsServer {

		private final Class<?> serviceClass;
		// 用链式的结构获取对应的资源.根元素为服务的地址
		// address[i] -> serviceBean[i] -> JaxWsServerFactoryBean[i]
		private Map<Object, Object> resources = new HashMap<Object, Object>();

		public JaxWsServer(Class<?> serviceClass) {
			this.serviceClass = serviceClass;
		}

		public Class<?> getServiceClass() {
			return serviceClass;
		}

		public void removeInstance(String address) {
			getServerFactoryBean(address).destroy();
			resources.remove(getServiceBean(address));
			resources.remove(address);
		}

		public void addServiceInstance(String address, ServerFactoryBean factoryBean) {
			resources.put(factoryBean.getAddress(), factoryBean.getServiceBean());
			resources.put(factoryBean.getServiceBean(), factoryBean);
		}

		public boolean hasServiceInstance() {
			return !resources.isEmpty();
		}

		/**
		 * 检查是否在指定地址发布了服务
		 * 
		 * @param address
		 * @return
		 */
		public boolean inUse(String address) {
			return resources.containsKey(address);
		}

		/**
		 * 该服务所有的服务地址
		 * 
		 * @return
		 */
		public List<String> listAddress() {
			List<String> addresses = new ArrayList<String>();
			Iterator<Object> iterator = resources.keySet().iterator();
			for (; iterator.hasNext();) {
				Object obj = iterator.next();
				if (obj instanceof String) {
					addresses.add((String) obj);
				}
			}
			return Collections.unmodifiableList(addresses);
		}

		public Object getServiceBean(String address) {
			return resources.get(address);
		}

		public ServerFactoryBean getServerFactoryBean(String address) {
			return (ServerFactoryBean) resources.get(getServiceBean(address));
		}

	}

}
