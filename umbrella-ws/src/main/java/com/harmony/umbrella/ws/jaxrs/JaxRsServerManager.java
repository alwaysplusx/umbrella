/*
 * Copyright 2013-2015 wuxii@foxmail.com.
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
package com.harmony.umbrella.ws.jaxrs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.core.BeanFactory;
import com.harmony.umbrella.core.SimpleBeanFactory;

/**
 * @author wuxii@foxmail.com
 */
public class JaxRsServerManager {

	private static final Logger log = LoggerFactory.getLogger(JaxRsServerManager.class);
	private static JaxRsServerManager instance;
	private BeanFactory beanFactory = new SimpleBeanFactory();

	private final Map<Class<?>, JaxRsServer> servers = new HashMap<Class<?>, JaxRsServer>();

	private JaxRsServerManager() {
	}

	public static JaxRsServerManager getInstance() {
		if (instance == null) {
			synchronized (JaxRsServerManager.class) {
				if (instance == null) {
					instance = new JaxRsServerManager();
				}
			}
		}
		return instance;
	}

	public void destory(Class<?> serviceClass, String address) {
		unregisterServer(serviceClass, address);
	}

	public boolean publish(Class<?> serviceClass, String address) {
		return publish(serviceClass, address, null);
	}

	// public boolean publish(Class<?> serviceClass, JaxRsMetadata loader) {
	// return publish(serviceClass, loader, null);
	// }

	public boolean publish(Class<?> serviceClass, String address, JaxRsFactoryConfig factoryConfig) {
		Object serviceBean = beanFactory.getBean(serviceClass);
		return publish(serviceClass, serviceBean, address, factoryConfig);
	}
	
	private boolean publish(Class<?> serviceClass, Object serviceBean, String address, JaxRsFactoryConfig factoryConfig) {
		if (inUse(address)) {
			if (isPublished(serviceClass, address)) {
				doUpdateService(serviceClass, serviceBean, address, factoryConfig);
				return true;
			}
			JaxRsServer server = getJaxRsServer(address);
			log.error("publish two different service[{}, {}] in same address {}", server.getServiceClass().getName(), serviceClass.getName(), address);
			return false;
		}
		JAXRSServerFactoryBean factoryBean = new JAXRSServerFactoryBean();
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
		log.debug("publish jax-rs service[{}] successfully", serviceBean.getClass().getName());
		registerServer(serviceClass, serviceBean, factoryBean);
		return true;
	}

	public boolean isPublished(Class<?> serviceClass, String address) {
		if (servers.containsKey(serviceClass)) {
			return servers.get(serviceClass).inUse(address);
		}
		return false;

	}

	public boolean inUse(String address) {
		return getJaxRsServer(address) != null;
	}

	private void unregisterServer(Class<?> serviceClass, String address) {
		JaxRsServer server = servers.get(serviceClass);
		server.getServerFactoryBean(address).getServer().destroy();
		server.resources.remove(server.getServiceBean(address));
		server.resources.remove(address);
		if (!server.existsServer()) {
			servers.remove(serviceClass);
		}
	}

	private void registerServer(Class<?> serviceClass, Object serviceBean, JAXRSServerFactoryBean factoryBean) {
		JaxRsServer server = null;
		if (!servers.containsKey(serviceClass)) {
			server = new JaxRsServer();
			servers.put(serviceClass, server);
		}
		server.serviceClass = serviceClass;
		server.resources.put(factoryBean.getAddress(), serviceBean);
		server.resources.put(serviceBean, factoryBean);
	}

	private void doUpdateService(Class<?> serviceClass, Object serviceBean, String address, JaxRsFactoryConfig factoryConfig) {
	}

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setBeanLoader(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	private JaxRsServer getJaxRsServer(String address) {
		Collection<JaxRsServer> values = servers.values();
		for (JaxRsServer server : values) {
			if (server.inUse(address)) {
				return server;
			}
		}
		return null;
	}

	class JaxRsServer {
		private Class<?> serviceClass;
		// address[i] -> serviceBean[i] -> JaxWsServerFactoryBean
		private Map<Object, Object> resources = new HashMap<Object, Object>();

		public Class<?> getServiceClass() {
			return serviceClass;
		}

		public boolean existsServer() {
			return resources.isEmpty();
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
			JAXRSServerFactoryBean factoryBean = getServerFactoryBean(address);
			return factoryBean.getServer();
		}

		public Object getServiceBean(String address) {
			return resources.get(address);
		}

		public JAXRSServerFactoryBean getServerFactoryBean(String address) {
			Object serviceBean = getServiceBean(address);
			return (JAXRSServerFactoryBean) resources.get(serviceBean);
		}

	}

	public interface JaxRsFactoryConfig {

		void config(JAXRSServerFactoryBean factoryBean);

	}
}
