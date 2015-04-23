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

import static com.harmony.umbrella.context.ee.metadata.BeanDefinitionManager.*;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.context.ApplicationContext;
import com.harmony.umbrella.context.ApplicationContextException;
import com.harmony.umbrella.context.ee.jmx.EJBContext;
import com.harmony.umbrella.context.ee.jmx.EJBContextMBean;
import com.harmony.umbrella.core.NoSuchBeanFindException;
import com.harmony.umbrella.util.ClassUtils;
import com.harmony.umbrella.util.JmxManager;
import com.harmony.umbrella.util.PropUtils;

/**
 * @author wuxii@foxmail.com
 */
public class EJBApplicationContext extends ApplicationContext implements EJBContextMBean {

	/**
	 * jndi默认配置文件地址
	 */
	public static final String JNDI_PROPERTIES_FILE_LOCATION = "META-INF/context/jndi.properties";

	private static final List<String> jndiPrefix = Collections.unmodifiableList(Arrays.asList("java:", ""));
	private static final Logger log = LoggerFactory.getLogger(EJBApplicationContext.class);
	private JndiNameFormat jndiFormat = new BeanJndiNameFormat();

	/**
	 * jndi文件地址
	 */
	private final String jndiPropertiesFileLocation;
	/**
	 * jndi配置属性
	 */
	private final Properties jndiProperties = new Properties();

	private JmxManager jmxManager = JmxManager.getInstance();

	private Map<Class<?>, SessionBean> sessionBeanMap = new HashMap<Class<?>, SessionBean>();

	private static EJBApplicationContext instance;

	private EJBApplicationContext() {
		this.jndiPropertiesFileLocation = PropUtils.getSystemProperty("jndi.properties.file", JNDI_PROPERTIES_FILE_LOCATION);
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

	public void init() {
		this.loadProperties();
		if (jmxManager.isRegistered(EJBContext.class)) {
			jmxManager.unregisterMBean(EJBContext.class);
		}
		jmxManager.registerMBean(new EJBContext(this));
	}

	/**
	 * 根据jndi名称获取JavaEE环境中指定的对象
	 * 
	 * @param jndiName
	 *            jndi名称
	 * @return 如果jndi名称指定的bean不存在返回{@code null}
	 */
	public Object lookup(String jndiName) throws ApplicationContextException {
		return lookup(jndiName, Object.class);
	}

	/**
	 * 根据jndi名称获取JavaEE环境中指定类型的对象
	 * 
	 * @param jndiName
	 *            jndi名称
	 * @param reqireType
	 *            指定的bean类型
	 * @return 如果jndi名称指定的bean不存在返回{@code null}
	 * @throws ClassCastException
	 *             找到的bean类型不为requireType
	 * @throws ApplicationContextException
	 *             初始化JavaEE环境失败
	 */
	@SuppressWarnings("unchecked")
	public <T> T lookup(String jndiName, Class<T> reqireType) throws ApplicationContextException {
		try {
			return (T) getContext().lookup(jndiName);
		} catch (NamingException e) {
			throw new ApplicationContextException(e.getMessage(), e.getCause());
		}
	}

	/**
	 * 从JavaEE环境中获取指定类实例 <p> <li>首先根据类型将解析clazz对应的jndi名称 <li>如果解析的jndi名称未找到对应类型的bean，
	 * 则通过递归 {@linkplain javax.naming.Context}中的内容查找 <li>若以上方式均为找到则返回{@code null}
	 * 
	 * @param clazz
	 *            待查找的类
	 * @return 指定类型的bean, 如果为找到返回{@code null}
	 * @throws ApplicationContextException
	 *             初始化JavaEE环境失败
	 */
	@SuppressWarnings("unchecked")
	public <T> T lookup(Class<T> clazz) throws ApplicationContextException {
		SessionBean sessionBean = sessionBeanMap.get(clazz);
		if (sessionBean != null) {
			// 已经将clazz解析过，明确clazz对应的一个jndi能找到指定类型的bean
			if (sessionBean.isCacheable()) {
				return sessionBean.getCachedBean();
			} else {
				return (T) lookup(sessionBean.getJndi());
			}
		}
		BeanDefinition bd = getBeanDefinition(clazz);
		try {
			sessionBean = tryLookup(bd);
		} catch (Exception e) {
			for (String prefix : jndiPrefix) {
				sessionBean = iterator(getContext(), prefix, bd);
				if (sessionBean != null) {
					break;
				}
			}
		}
		if (sessionBean != null) {
			sessionBeanMap.put(clazz, sessionBean);
			return sessionBean.getCachedBean();
		}
		return null;
	}

	private SessionBean tryLookup(BeanDefinition bd) throws Exception {
		String jndi = jndiFormat.format(bd);
		Object bean = getContext().lookup(jndi);
		if (bean != null) {
			bean = bd.getBeanClass().cast(bean);
		}
		return new SessionBean(jndi, bd, bean);
	}

	/**
	 * 遍历{@linkplain Context}来查找指定类型的bean
	 * 
	 * @param context
	 *            JavaEE环境上下文
	 * @param root
	 *            context的根节点
	 * @param clazz
	 *            指定需要的类型
	 * @return 如果为找到返回{@code null}
	 */
	private SessionBean iterator(Context context, String root, BeanDefinition bd) {
		Class<?> clazz = bd.getBeanClass();
		try {
			Object bean = context.lookup(root);
			if (bean instanceof Context) {
				NamingEnumeration<NameClassPair> ne = ((Context) bean).list("");
				while (ne.hasMore()) {
					NameClassPair nameClassPair = ne.next();
					String jndi = root + ("".equals(root) ? "" : "/") + nameClassPair.getName();
					SessionBean sessionBean = iterator(context, jndi, bd);
					if (sessionBean != null)
						return sessionBean;
				}
			} else if (clazz.isInstance(bean) || bd.getSuitableRemoteClass().isInstance(bean)) {
				// TODO 如果是类，应该使用接口类去比较 interfaceClass.isInstance(bean);
				return new SessionBean(root, bd, bean);
			}
		} catch (Exception e) {
			log.debug("", e);
		}
		return null;
	}

	/**
	 * 初始化客户端JavaEE环境 <p> 默认加载指定路径下 {@link #JNDI_PROPERTIES_FILE_LOCATION} 的资源文件作为初始化属性
	 * 
	 * @return
	 * @throws ApplicationContextException
	 *             初始化上下文失败
	 */
	public Context getContext() {
		try {
			return new InitialContext(jndiProperties);
		} catch (NamingException e) {
			throw new ApplicationContextException(e.getMessage(), e.getCause());
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
	 * @throws ApplicationContextException
	 *             上下文初始化失败
	 */
	public static Object lookup(String jndiName, Properties jndiProperties) {
		try {
			return new InitialContext(jndiProperties).lookup(jndiName);
		} catch (NamingException e) {
			throw new ApplicationContextException(e.getMessage(), e.getCause());
		}
	}

	/*
	 * JMX method
	 */
	@Override
	public void loadProperties() {
		try {
			jndiProperties.putAll(PropUtils.loadProperties(jndiPropertiesFileLocation));
		} catch (IOException e) {
			log.warn("can't load jndi properties file", e);
		}
	}

	/*
	 * JMX method
	 */
	@Override
	public void cleanProperties() {
		jndiProperties.clear();
		log.debug("jndi properties cleaned");
	}

	/*
	 * JMX method
	 */
	@Override
	public String jndiPropertiesFilePath() {
		URL url = ClassUtils.getDefaultClassLoader().getResource(jndiPropertiesFileLocation);
		if (url == null) {
			return "error: not exist properties file [" + jndiPropertiesFileLocation + "] in classpath";
		}
		return url.getFile();
	}

	public void destory() {
		jmxManager.unregisterMBean(EJBContext.class);
		this.cleanProperties();
	}

	// ###################
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getBean(String beanName) throws NoSuchBeanFindException {
		Object bean = null;
		try {
			bean = lookup(beanName);
		} catch (ApplicationContextException e) {
			try {
				bean = getBean(Class.forName(beanName));
			} catch (Exception e1) {
			}
		}
		if (bean == null) {
			throw new NoSuchBeanFindException(beanName + " not find!");
		}
		return (T) bean;
	}

	@Override
	public <T> T getBean(String beanName, String scope) throws NoSuchBeanFindException {
		return getBean(beanName);
	}

	@Override
	public <T> T getBean(Class<T> beanClass) throws NoSuchBeanFindException {
		T bean = null;
		try {
			bean = lookup(beanClass);
		} catch (ApplicationContextException e) {
			throw new NoSuchBeanFindException(beanClass + " not find!", e);
		}
		return bean;
	}

	@Override
	public <T> T getBean(Class<T> beanClass, String scope) throws NoSuchBeanFindException {
		return getBean(beanClass);
	}

	@Override
	public boolean exixts(Class<?> clazz) {
		return getBean(clazz) != null;
	}

	protected SessionBean createSessionBean(String jndiName, BeanDefinition bd, Object cacheObject) {
		return new SessionBean(jndiName, bd, cacheObject);
	}

}
