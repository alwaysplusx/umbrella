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
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.harmony.umbrella.context.ApplicationContext;
import com.harmony.umbrella.context.ApplicationContextException;
import com.harmony.umbrella.context.ee.jmx.EJBContext;
import com.harmony.umbrella.context.ee.jmx.EJBContextMBean;
import com.harmony.umbrella.context.ee.reader.WeblogicContextReader;
import com.harmony.umbrella.core.NoSuchBeanFindException;
import com.harmony.umbrella.util.ClassUtils;
import com.harmony.umbrella.util.JmxManager;
import com.harmony.umbrella.util.PropUtils;

/**
 * JavaEE的应用上下文实现
 * 
 * @author wuxii@foxmail.com
 */
public class EJBApplicationContext extends ApplicationContext implements EJBContextMBean {

	private static final String[] JNDI_CONTEXT_ROOT_ARRAY = { "", "java:" };

	/**
	 * jndi默认配置文件地址
	 */
	public static final String JNDI_PROPERTIES_FILE_LOCATION = "META-INF/context/jndi.properties";

	public static final List<String> JNDI_CONTEXT_ROOT = Collections.unmodifiableList(Arrays.asList(JNDI_CONTEXT_ROOT_ARRAY));

	/**
	 * jndi文件地址
	 */
	private final String jndiPropertiesFileLocation;
	/**
	 * jndi开始的上下文根
	 */
	private final String[] jndiContextRoot;
	/**
	 * jndi配置属性
	 */
	private Properties jndiProperties = new Properties();

	/**
	 * 遍历context最大等待时间, default 30000ms
	 */
	private long maxWait;

	/**
	 * JMX管理
	 */
	private JmxManager jmxManager = JmxManager.getInstance();

	/**
	 * 存放与class对应的Context的bean信息，信息中包含jndi, bean definition, 和一个被缓存着的实例(可以做单例使用)
	 */
	private Map<Class<?>, SessionBean> sessionBeanMap = new HashMap<Class<?>, SessionBean>();

	/**
	 * 根据实际环境加载不同的{@linkplain BeanContextResolver}
	 */
	private BeanContextResolver beanContextResolver;

	private static EJBApplicationContext instance;

	private int status = CREATE;

	private EJBApplicationContext() {
		this.jndiPropertiesFileLocation = PropUtils.getSystemProperty("jndi.properties.file", JNDI_PROPERTIES_FILE_LOCATION);
		this.loadProperties();
		this.beanContextResolver = new GenericBeanContextResolver(jndiProperties);
		this.jndiContextRoot = jndiProperties.getProperty("jndi.context.root") == null ? JNDI_CONTEXT_ROOT_ARRAY : jndiProperties.getProperty("jndi.context.root").split(",");
		this.maxWait = Long.parseLong(jndiProperties.getProperty("jndi.context.waitTime", "30000"));
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

	/*
	 * 初始化加载属性文件，给应用注册JMX
	 * 
	 * @see com.harmony.umbrella.context.ApplicationContext#init()
	 */
	public void init() {
		if (CREATE == status) {
			if (jmxManager.isRegistered(EJBContext.class)) {
				jmxManager.unregisterMBean(EJBContext.class);
			}
			jmxManager.registerMBean(new EJBContext(this));
			LOG.debug("init ejb application success");
			this.status = INITIALIZED;
		}
	}

	/**
	 * 根据jndi名称获取JavaEE环境中指定的对象
	 * 
	 * @param jndi
	 *            jndi名称
	 * @return 指定jndi的bean
	 * @throws ApplicationContextException
	 *             不存在指定的jndi
	 */
	public Object lookup(String jndi) throws ApplicationContextException {
		return lookup(jndi, Object.class);
	}

	/**
	 * 根据jndi名称获取JavaEE环境中指定类型的对象
	 * 
	 * @param jndi
	 *            jndi名称
	 * @param reqireType
	 *            指定的bean类型
	 * @return 指定jndi的bean
	 * @throws ClassCastException
	 *             找到的bean类型不为requireType
	 * @throws ApplicationContextException
	 *             不存在指定的jndi
	 */
	@SuppressWarnings("unchecked")
	public <T> T lookup(String jndi, Class<T> reqireType) throws ApplicationContextException {
		try {
			return (T) getContext().lookup(jndi);
		} catch (NamingException e) {
			LOG.error("jndi not find {}", jndi, e);
			throw new ApplicationContextException(e.getMessage(), e.getCause());
		}
	}

	/**
	 * 从JavaEE环境中获取指定类实例
	 * <p>
	 * <li>首先根据所运行的容器不同通过 {@linkplain BeanContextResolver} 将 {@code clazz}
	 * 格式化为特定的{@code jndi}
	 * <li>如果格式化后的jndi名称未找到对应类型的bean， 则通过 {@linkplain ContextReader}
	 * 递归读取上下文查找需要指定的内容
	 * <li>若以上方式均为找到则返回{@code null}
	 * 
	 * @param clazz
	 *            待查找的类
	 * @param mappedName
	 *            bean的映射名称
	 * @return 指定类型的bean
	 * @throws ApplicationContextException
	 *             不存在指定的jndi
	 */
	@SuppressWarnings("unchecked")
	public <T> T lookup(Class<T> clazz, String mappedName) throws ApplicationContextException {
		SessionBean sessionBean = sessionBeanMap.get(clazz);
		if (sessionBean != null) {
			LOG.debug("lookup bean[{}] use cached session bean {}", sessionBean.getJndi(), sessionBean);
			// 已经将clazz解析过，明确clazz对应的一个jndi能找到指定类型的bean
			if (sessionBean.isCacheable()) {
				return (T) sessionBean.getBean();
			} else {
				try {
					return (T) lookup(sessionBean.getJndi());
				} catch (Exception e) {
					// jndi changed
					LOG.warn("use cached session bean can't lookup bean{}, remove it {}. and try lookup bean as new one", sessionBean.getJndi(), sessionBean);
					sessionBeanMap.remove(clazz);
				}
			}
		}
		// 初次解析
		BeanDefinition bd = new BeanDefinition(clazz, mappedName);
		try {
			sessionBean = tryLookup(bd);
			LOG.info("lookup bean typeof {} by jndi[{}]", clazz, sessionBean.getJndi());
		} catch (Exception e) {
			for (String root : jndiContextRoot) {
				sessionBean = iterator(bd, root);
				if (sessionBean != null) {
					LOG.info("find bean typeof {}, with jndi[{}]", clazz, sessionBean.getJndi());
					break;
				}
			}
			if (sessionBean == null) {
				StringBuilder message = new StringBuilder("can't find bean type of ");
				message.append(clazz);
				if (bd.getMappedName() != null) {
					message.append(", mappend name is ").append(bd.getMappedName());
				}
				LOG.error(message.toString());
				throw new ApplicationContextException(message.toString());
			}
		}
		sessionBeanMap.put(clazz, sessionBean);
		LOG.debug("put session bean[{}] in cached", sessionBean);
		return (T) sessionBean.getBean();
	}

	/**
	 * 尝试使用jndi去加载bean
	 * 
	 * @param bd
	 *            beanDefinition
	 * @return SessionBean
	 * @throws Exception
	 *             如果未找到bean
	 */
	protected SessionBean tryLookup(BeanDefinition bd) throws Exception {
		String jndi = null;
		try {
			jndi = beanContextResolver.resolveBeanName(bd);
			Object bean = getContext().lookup(jndi);
			if (beanContextResolver.isDeclareBean(bd, bean)) {
				return new SessionBeanImpl(bd, jndi, bean);
			}
		} catch (Exception e) {
			LOG.debug("can't lookup jndi[{}], try to iterator context find bean of {}", jndi, bd.getBeanClass(), e);
			throw e;
		}
		throw new Exception("for entry catch block");
	}

	protected SessionBean iterator(final BeanDefinition bd, final String root) {
		final SessionBeanImpl sessionBean = new SessionBeanImpl(bd);
		getContextReader().accept(new ContextVisitor() {

			@Override
			public void visitContext(Context context, String jndi) {
				System.err.println(">>>> " + jndi);
			}

			@Override
			public void visitBean(Object bean, String jndi) {
				System.err.println(">>>> " + jndi);
				if (beanContextResolver.isDeclareBean(bd, bean)) {
					sessionBean.bean = bean;
					sessionBean.jndi = jndi;
					this.visitEnd();
				}
			}

		}, root);
		return (sessionBean.bean != null && sessionBean.jndi != null) ? sessionBean : null;
	}

	/**
	 * 从JavaEE环境中获取指定类实例
	 * <p>
	 * <li>首先根据类型将解析clazz对应的jndi名称
	 * <li>如果解析的jndi名称未找到对应类型的bean， 则通过递归 {@linkplain javax.naming.Context}
	 * 中的内容查找
	 * <li>若以上方式均为找到则返回{@code null}
	 * 
	 * @param clazz
	 *            待查找的类
	 * @return 指定类型的bean, 如果为找到返回{@code null}
	 * @throws ApplicationContextException
	 *             初始化JavaEE环境失败
	 */
	public <T> T lookup(Class<T> clazz) throws ApplicationContextException {
		return lookup(clazz, null);
	}

	/**
	 * 初始化客户端JavaEE环境
	 * <p>
	 * 默认加载指定路径下 {@link #JNDI_PROPERTIES_FILE_LOCATION} 的资源文件作为初始化属性
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

	protected ContextReader getContextReader() {
		return new WeblogicContextReader(getContext(), maxWait);
	}

	public void destory() {
		if (INITIALIZED == status) {
			jmxManager.unregisterMBean(EJBContext.class);
			this.cleanProperties();
			status = DESTROY;
		}
	}

	public void setBeanContextResolver(BeanContextResolver beanContextResolver) {
		this.beanContextResolver = beanContextResolver;
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

	// ################### BeanFactory #################

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

	// ############## JMX ############
	/*
	 * JMX method
	 */
	@Override
	public void loadProperties() {
		try {
			jndiProperties.putAll(PropUtils.loadProperties(jndiPropertiesFileLocation));
		} catch (IOException e) {
			LOG.warn("can't load jndi properties file", e);
		}
	}

	/*
	 * JMX method
	 */
	@Override
	public void cleanProperties() {
		jndiProperties.clear();
		LOG.debug("jndi properties cleaned");
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

	/*
	 * JMX method
	 */
	@Override
	public boolean exixts(String className) {
		return getBean(className) != null;
	}

	private class SessionBeanImpl implements SessionBean {

		private final BeanDefinition bd;
		private Object bean;
		private String jndi;

		public SessionBeanImpl(BeanDefinition bd) {
			this.bd = bd;
		}

		public SessionBeanImpl(BeanDefinition bd, String jndi, Object bean) {
			this.bd = bd;
			this.bean = bean;
			this.jndi = jndi;
		}

		@Override
		public Object getBean() {
			return bean;
		}

		@Override
		public String getJndi() {
			return jndi;
		}

		@Override
		public boolean isCacheable() {
			return bd.isSessionBean();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((bd == null) ? 0 : bd.hashCode());
			result = prime * result + ((bean == null) ? 0 : bean.hashCode());
			result = prime * result + ((jndi == null) ? 0 : jndi.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			SessionBeanImpl other = (SessionBeanImpl) obj;
			if (bd == null) {
				if (other.bd != null)
					return false;
			} else if (!bd.equals(other.bd))
				return false;
			if (bean == null) {
				if (other.bean != null)
					return false;
			} else if (!bean.equals(other.bean))
				return false;
			if (jndi == null) {
				if (other.jndi != null)
					return false;
			} else if (!jndi.equals(other.jndi))
				return false;
			return true;
		}

	}

}
