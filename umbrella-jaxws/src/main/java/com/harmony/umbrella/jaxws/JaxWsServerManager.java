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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.core.BeanLoader;
import com.harmony.umbrella.core.ClassBeanLoader;

/**
 * 基于cxf的WebService服务管理
 * 
 * @author wuxii@foxmail.com
 */
public class JaxWsServerManager {

	private static final Logger log = LoggerFactory.getLogger(JaxWsServerManager.class);
	private final Map<Class<?>, JaxWsServer> servers = new HashMap<Class<?>, JaxWsServer>();

	private static JaxWsServerManager instance;
	private BeanLoader beanLoader = new ClassBeanLoader();

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
	 * 销毁说有已经创建的服务
	 * 
	 * @param serviceClass
	 * @param address
	 */
	public void destory(Class<?> serviceClass, String address) {
		unregisterServer(serviceClass, address);
	}

	/**
	 * 在指定地址发布一个服务
	 * 
	 * @param serviceClass
	 * @param address
	 * @return
	 */
	public boolean publish(Class<?> serviceClass, String address) {
		return publish(serviceClass, address, null);
	}

	/**
	 * 使用指定的服务元数据发布服务
	 * 
	 * @param serviceClass
	 * @param loader
	 * @return
	 */
	public boolean publish(Class<?> serviceClass, MetadataLoader loader) {
		return publish(serviceClass, loader, null);
	}

	/**
	 * 在指定地址发布服务，发布前提供配置服务
	 * 
	 * @param serviceClass
	 * @param address
	 * @param factoryConfig
	 * @return
	 */
	public boolean publish(Class<?> serviceClass, String address, JaxWsServerFactoryConfig factoryConfig) {
		Object serviceBean = beanLoader.loadBean(serviceClass, BeanLoader.PROTOTYPE);
		return publish(serviceClass, serviceBean, address, factoryConfig);
	}

	/**
	 * 可配置的发布服务
	 * 
	 * @param serviceClass
	 * @param loader
	 * @param factoryConfig
	 * @return
	 */
	public boolean publish(Class<?> serviceClass, MetadataLoader loader, JaxWsServerFactoryConfig factoryConfig) {
		Object serviceBean = beanLoader.loadBean(serviceClass, BeanLoader.PROTOTYPE);
		String address = loader.getAddress(serviceClass);
		return publish(serviceClass, serviceBean, address, factoryConfig);
	}

	private boolean publish(Class<?> serviceClass, Object serviceBean, String address, JaxWsServerFactoryConfig factoryConfig) {
		if (inUse(address)) {
			if (isPublished(serviceClass, address)) {
				doUpdateService(serviceClass, serviceBean, address, factoryConfig);
				return true;
			}
			JaxWsServer server = getJaxWsServer(address);
			log.error("publish two different service[{}, {}] in same address {}", server.getServiceClass().getName(), serviceClass.getName(), address);
			return false;
		}
		JaxWsServerFactoryBean factoryBean = new JaxWsServerFactoryBean();
		if (factoryConfig != null) {
			factoryConfig.config(factoryBean);
		}
		if (factoryBean.getServer() != null) {
			log.error("config factory not allow to create server");
			throw new IllegalStateException("config factory not allow to create server");
		}
		factoryBean.setAddress(address);
		factoryBean.setServiceBean(serviceBean);
		factoryBean.create();
		log.debug("publish jax-ws service[{}] successfully", serviceBean.getClass().getName());
		registerServer(serviceClass, factoryBean);
		return true;
	}

	private JaxWsServer getJaxWsServer(String address) {
		Collection<JaxWsServer> values = servers.values();
		for (JaxWsServer server : values) {
			if (server.inUse(address))
				return server;
		}
		return null;
	}

	/**
	 * 检测地址是否已经发布了服务
	 * 
	 * @param address
	 * @return
	 */
	public boolean inUse(String address) {
		return getJaxWsServer(address) != null;
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

	private void doUpdateService(Class<?> serviceClass, Object serviceBean, String address, JaxWsServerFactoryConfig factoryConfig) {
		// do nothing
	}

	private void unregisterServer(Class<?> serviceClass, String address) {
		JaxWsServer server = servers.get(serviceClass);
		server.getServerFactoryBean(address).destroy();
		server.resources.remove(server.getServiceBean(address));
		server.resources.remove(address);
		if (!server.existsServer()) {
			servers.remove(serviceClass);
		}
	}

	private void registerServer(Class<?> serviceClass, JaxWsServerFactoryBean factoryBean) {
		JaxWsServer server = null;
		if (!servers.containsKey(serviceClass)) {
			server = new JaxWsServer();
			servers.put(serviceClass, server);
		}
		server.serviceClass = serviceClass;
		server.resources.put(factoryBean.getAddress(), factoryBean.getServiceBean());
		server.resources.put(factoryBean.getServiceBean(), factoryBean);
	}

	public BeanLoader getBeanLoader() {
		return beanLoader;
	}

	public void setBeanLoader(BeanLoader beanLoader) {
		this.beanLoader = beanLoader;
	}

	class JaxWsServer {

		private Class<?> serviceClass;
		// address[i] -> serviceBean[i] -> JaxWsServerFactoryBean
		private Map<Object, Object> resources = new HashMap<Object, Object>();

		public Class<?> getServiceClass() {
			return serviceClass;
		}

		public boolean existsServer() {
			return !resources.isEmpty();
		}

		public boolean inUse(String address) {
			return resources.containsKey(address);
		}

		public List<String> listAddress() {
			List<String> address = new ArrayList<String>();
			Iterator<Object> iterator = resources.keySet().iterator();
			for (; iterator.hasNext();) {
				Object obj = iterator.next();
				if (obj instanceof String) {
					address.add((String) obj);
				}
			}
			return address;
		}

		public Server getServer(String address) {
			JaxWsServerFactoryBean factoryBean = getServerFactoryBean(address);
			return factoryBean.getServer();
		}

		public Object getServiceBean(String address) {
			return resources.get(address);
		}

		public JaxWsServerFactoryBean getServerFactoryBean(String address) {
			Object serviceBean = getServiceBean(address);
			return (JaxWsServerFactoryBean) resources.get(serviceBean);
		}

	}

	/**
	 * 服务创建前的回调配置
	 * 
	 * @author wuxii@foxmail.com
	 */
	public interface JaxWsServerFactoryConfig {

		void config(JaxWsServerFactoryBean factoryBean);

	}

}
