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
package com.harmony.umbrella.context.ee;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.ejb.Remote;
import javax.ejb.Singleton;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.context.ApplicationContext;
import com.harmony.umbrella.context.ContextException;
import com.harmony.umbrella.context.ee.jmx.EJBContextMBean;
import com.harmony.umbrella.util.ClassUtils;
import com.harmony.umbrella.util.JmxManager;
import com.harmony.umbrella.util.PropUtils;

public class EJBApplicationContext implements ApplicationContext, EJBContextMBean {

	public static final String JNDI_PROPERTIES_FILE = "META-INF/jndi.properties";

	private static final String[] jndiPrefix = new String[] { "java:", "" };
	private static final Logger log = LoggerFactory.getLogger(EJBApplicationContext.class);

	private final String jndiPropertiesFile;
	private JmxManager jmxManager = JmxManager.getInstance();
	private Map<Class<?>, Holder> classAndBeanMap = new HashMap<Class<?>, Holder>();

	private static EJBApplicationContext instance;
	private Properties jndiProperties = new Properties();

	private EJBApplicationContext() {
		this.jndiPropertiesFile = PropUtils.getSystemProperty("jndi.properties.file", JNDI_PROPERTIES_FILE);
		if (jmxManager.isRegistered(this)) {
			jmxManager.unregisterMBean(this);
		}
		jmxManager.registerMBean(this);
	}

	public static EJBApplicationContext getInstance() {
		if (instance == null) {
			synchronized (EJBApplicationContext.class) {
				if (instance == null) {
					instance = new EJBApplicationContext();
				}
			}
		}
		return instance;
	}

	/**
	 * 根据JNDI名称获取JavaEE环境中指定的对象
	 * 
	 * @param jndiName
	 * @return
	 */
	public Object lookup(String jndiName) {
		try {
			return getContext().lookup(jndiName);
		} catch (NamingException e) {
			throw new ContextException(e);
		}
	}

	/**
	 * 在JavaEE环境中查找指定的类实例
	 * 
	 * @param clazz
	 *            待查询的类
	 * @param cacheable
	 *            是否接受从缓存中获取
	 * @return
	 * @throws com.harmony.umbrella.context.moon.util.ejb.ContextException
	 *             未找到对应实例
	 */
	@SuppressWarnings("unchecked")
	public <T> T lookup(Class<T> clazz, boolean cacheable) {
		if (classAndBeanMap.containsKey(clazz)) {
			Holder holder = classAndBeanMap.get(clazz);
			if (cacheable) {
				return (T) holder.beanInstance;
			}
		}
		return lookup(clazz);
	}

	/**
	 * 从JavaEE环境中获取指定类实例
	 * 
	 * @param clazz
	 *            待查找的类
	 * @return
	 * @throws com.harmony.umbrella.context.moon.util.ejb.ContextException
	 *             未找到对应实例
	 */
	@SuppressWarnings("unchecked")
	public <T> T lookup(Class<T> clazz) {
		if (classAndBeanMap.containsKey(clazz)) {
			Holder holder = classAndBeanMap.get(clazz);
			holder.beanInstance = lookup(holder.jndiName);
			try {
				return (T) holder.beanInstance;
			} catch (ContextException e) {
				classAndBeanMap.remove(clazz);
			}
		}
		try {
			String jndiName = toJndiName(clazz);
			Object object = lookup(jndiName);
			classAndBeanMap.put(clazz, new Holder(jndiName, object));
			return (T) object;
		} catch (ContextException e) {
			for (String prefix : jndiPrefix) {
				try {
					Holder holder = iterator(getContext(), prefix, clazz);
					if (holder != null) {
						classAndBeanMap.put(clazz, holder);
						return (T) holder.beanInstance;
					}
				} catch (Exception e1) {
					log.error("", e1);
				}
			}
		}
		throw new ContextException("can't lookup class[" + clazz.getName() + "] ejb bean");
	}

	private Holder iterator(Context context, String root, Class<?> instanceClass) {
		try {
			Object obj = context.lookup(root);
			if (obj instanceof Context) {
				NamingEnumeration<NameClassPair> ne = ((Context) obj).list("");
				while (ne.hasMore()) {
					NameClassPair nameClassPair = ne.next();
					String jndi = root + ("".equals(root) ? "" : "/") + nameClassPair.getName();
					Holder holder = iterator(context, jndi, instanceClass);
					if (holder != null)
						return holder;
				}
			} else if (instanceClass.isInstance(obj)) {
				return new Holder(root, obj);
			}
		} catch (Exception e) {
			log.debug("", e);
		}
		return null;
	}

	/**
	 * 获取JavaEE的JNDI上下文 <p> 默认加载指定路径下 {@link EJBContextMBean#JNDI_PROPERTIES_FILE}
	 * 的资源文件作为初始化属性
	 * 
	 * @return
	 * @throws ContextException
	 *             初始化上下文失败
	 */
	public Context getContext() {
		this.loadProperties(false);
		try {
			return new InitialContext(jndiProperties);
		} catch (NamingException e) {
			throw new ContextException(e);
		}
	}

	/**
	 * 在指定上下文属性中查找对于jndi名称的对象
	 * 
	 * @param jndiName
	 *            jndi名称
	 * @param jndiProperties
	 *            指定上下文属性
	 * @return
	 * @throws ContextException
	 *             上下文初始化失败
	 */
	public static Object lookup(String jndiName, Properties jndiProperties) {
		try {
			return new InitialContext(jndiProperties).lookup(jndiName);
		} catch (NamingException e) {
			throw new ContextException(e);
		}
	}

	/**
	 * 将类转为对应的jndi名称 <p> 可通过{@link EJBContextMBean#jndi_Properties_File}
	 * 中moon.jndi.name.format来定制格式
	 * 
	 * @param clazz
	 * @return
	 */
	public String toJndiName(Class<?> clazz) {
		this.loadProperties(false);
		StringBuffer sb = new StringBuffer();
		// TODO 根据jndiFormat生成jndiName
		// final String jndiFormat =
		// jndiProperties.getProperty("jee.jndi.format");
		String beanName = getBeanName(clazz);
		sb.append(beanName).append("#");
		String interfaceType = clazz.getName();
		if (!clazz.isInterface()) {
			Remote remoteAnn = clazz.getAnnotation(Remote.class);
			if (remoteAnn != null) {
				Class<?>[] value = remoteAnn.value();
				if (value.length != 0) {
					interfaceType = value[0].getName();
				}
			} else {
				Class<?>[] interfaces = clazz.getInterfaces();
				for (Class<?> c : interfaces) {
					if (c.getName().endsWith("Remote") || c.getAnnotation(Remote.class) != null) {
						interfaceType = c.getName();
					}
				}
			}
		}
		sb.append(interfaceType);
		return sb.toString();
	}

	private String getBeanName(Class<?> clazz) {
		String beanName = null;
		final String beanNameSuffix = jndiProperties.getProperty("jee.jndi.beanName.suffix", "Bean");
		if (clazz.isInterface()) {
			final String interfaceClassSuffix = jndiProperties.getProperty("jee.jndi.interfaceClass.suffix", "Remote");
			String interfaceClassName = clazz.getSimpleName();
			int suffixIndex = interfaceClassName.lastIndexOf(interfaceClassSuffix);
			if (suffixIndex != -1) {
				beanName = interfaceClassName.substring(0, suffixIndex) + beanNameSuffix;
			} else {
				beanName = interfaceClassName + beanNameSuffix;
			}
		} else {
			Annotation ann = clazz.getAnnotation(Singleton.class);
			if (ann != null) {
				beanName = "".equals(((Singleton) ann).mappedName()) ? clazz.getSimpleName() : ((Singleton) ann).mappedName();
			}
			ann = clazz.getAnnotation(Stateful.class);
			if (ann != null) {
				beanName = "".equals(((Stateful) ann).mappedName()) ? clazz.getSimpleName() : ((Stateful) ann).mappedName();
			}
			ann = clazz.getAnnotation(Stateless.class);
			if (ann != null) {
				beanName = "".equals(((Stateless) ann).mappedName()) ? clazz.getSimpleName() : ((Stateless) ann).mappedName();
			}
			if (beanName == null) {
				beanName = clazz.getSimpleName();
			}
		}
		return beanName;
	}

	@Override
	public void loadProperties(boolean mandatory) {
		if (jndiProperties.isEmpty() || mandatory) {
			try {
				jndiProperties.putAll(PropUtils.loadProperties(jndiPropertiesFile));
			} catch (IOException e) {
				log.warn("can't load jndi properties file", e);
			}
		}
	}

	@Override
	public void cleanProperties() {
		jndiProperties = null;
		log.debug("jndi properties cleaned");
	}

	@Override
	public String jndiPropertiesFilePath() {
		URL url = ClassUtils.getDefaultClassLoader().getResource(jndiPropertiesFile);
		if (url == null) {
			return "error: not exist properties file [" + jndiPropertiesFile + "] in classpath";
		}
		return url.getFile();
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		jmxManager.unregisterMBean(this);
	}

	private static class Holder {
		String jndiName;
		Object beanInstance;

		public Holder(String jndiName, Object instance) {
			this.jndiName = jndiName;
			this.beanInstance = instance;
		}

		@Override
		public String toString() {
			return "Holder [jndiName=" + jndiName + ", bean=" + beanInstance + "]";
		}
	}

}
