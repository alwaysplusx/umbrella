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
package com.harmony.modules.jaxws.support;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.modules.bean.BeanLoader;
import com.harmony.modules.bean.ClassBeanLoader;
import com.harmony.modules.jaxws.JaxWsContext;
import com.harmony.modules.jaxws.JaxWsContextHandler;
import com.harmony.modules.jaxws.JaxWsExecutor;
import com.harmony.modules.jaxws.MetadataLoader;
import com.harmony.modules.jaxws.SimpleJaxWsContext;
import com.harmony.modules.jaxws.impl.JaxWsCXFExecutor;

/**
 * @author wuxii@foxmail.com
 */
public class JaxWsContextReceiverBean implements JaxWsContextReceiver {

	protected final static String JAXWS_HANDLERS = "META-INF/jaxws/jaxws-receiver.txt";

	private Logger log = LoggerFactory.getLogger(JaxWsContextReceiverBean.class);
	private MetadataLoader metaLoader;
	/**
	 * 单独实例，莫要与ejb环境中的共用
	 */
	private JaxWsExecutor executor = new JaxWsCXFExecutor();
	private boolean reload = true;
	private final String handlersLocation;
	private BeanLoader beanLoader = new ClassBeanLoader();

	public JaxWsContextReceiverBean(String handlerLocation) {
		this.handlersLocation = handlerLocation;
		InputStream inStream = null;
		try {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			inStream = loader.getResourceAsStream(handlersLocation);
			if (inStream != null) {
				Properties props = new Properties();
				props.load(inStream);
				initHandler(props);
			}
		} catch (IOException e) {
			log.error("加载资源文件出错", handlerLocation);
			log.error("", e);
		} finally {
			try {
				if (inStream != null) {
					inStream.close();
				}
			} catch (IOException e) {
				log.debug("关闭资源文件{}出错", handlersLocation);
				log.debug("", e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void initHandler(Properties props) {
		Set<String> names = props.stringPropertyNames();
		for (String name : names) {
			if (!Boolean.valueOf(props.getProperty(name)))
				continue;
			try {
				Class<?> clazz = Class.forName(name);
				if (JaxWsContextHandler.class.isAssignableFrom(clazz)) {
					JaxWsContextHandler instance = this.newInstance((Class<? extends JaxWsContextHandler>) clazz);
					if (instance != null) {
						this.addHandler(instance);
						continue;
					}
					log.warn("无法初始化{}", clazz);
				}
				log.warn("{}不为{}的子类", clazz, JaxWsContextHandler.class);
			} catch (ClassNotFoundException e) {
				log.warn("class not find {}", name);
			}
		}
	}

	protected JaxWsContextHandler newInstance(Class<? extends JaxWsContextHandler> clazz) {
		try {
			return beanLoader.loadBean(clazz);
		} catch (IllegalArgumentException e) {
			log.error("无法初始化{}", clazz);
			log.error("", e);
		}
		return null;
	}

	public JaxWsContextReceiverBean() {
		this(JAXWS_HANDLERS);
	}

	@Override
	public void receive(JaxWsContext context) {
		try {
			context = reloadContext(context);
			executor.execute(context);
		} catch (Exception e) {
			log.warn("执行交互异常" + context, e);
		}
	}

	@Override
	public JaxWsExecutor getJaxWsExecutor() {
		return this.executor;
	}

	@Override
	public void open() throws Exception {
		// do nothing
	}

	@Override
	public void close() throws Exception {
		// do nothing
	}

	@Override
	public boolean isClosed() {
		return false;
	}

	protected void setJaxWsExecutor(JaxWsExecutor executor) {
		this.executor = executor;
	}

	public void setMetadataLoader(MetadataLoader metaLoader) {
		this.metaLoader = metaLoader;
	}

	public boolean addHandler(JaxWsContextHandler handler) {
		return getJaxWsExecutor().addHandler(handler);
	}

	public boolean removeHandler(JaxWsContextHandler handler) {
		return getJaxWsExecutor().removeHandler(handler);
	}

	public String getHandlersLocation() {
		return handlersLocation;
	}

	protected JaxWsContext reloadContext(JaxWsContext context) {
		if (!reload && context.getAddress() != null)
			return context;
		SimpleJaxWsContext copyContext = new SimpleJaxWsContext();
		Class<?> serviceInterface = context.getServiceInterface();
		copyContext = new SimpleJaxWsContext(serviceInterface, context.getMethodName(), context.getParameters());
		copyContext.setAddress(metaLoader.getAddress(serviceInterface));
		copyContext.setUsername(metaLoader.getUsername(serviceInterface));
		copyContext.setPassword(metaLoader.getPassword(serviceInterface));
		Enumeration<String> names = context.getContextHeaderNames();
		for (; names.hasMoreElements();) {
			String name = names.nextElement();
			copyContext.put(name, context.get(name));
		}
		return copyContext;
	}

	public void setMetaLoader(MetadataLoader metaLoader) {
		this.metaLoader = metaLoader;
	}

	public void setReload(boolean reload) {
		this.reload = reload;
	}

	public void setBeanLoader(BeanLoader beanLoader) {
		this.beanLoader = beanLoader;
	}

}
