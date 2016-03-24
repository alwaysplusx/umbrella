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

import static com.harmony.umbrella.context.ee.ResolverManager.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.harmony.umbrella.context.ApplicationContext;
import com.harmony.umbrella.context.ApplicationContextException;
import com.harmony.umbrella.context.ee.jmx.EJBContext;
import com.harmony.umbrella.context.ee.jmx.EJBContextMBean;
import com.harmony.umbrella.core.BeanFactory;
import com.harmony.umbrella.core.NoSuchBeanFoundException;
import com.harmony.umbrella.util.ClassUtils;
import com.harmony.umbrella.util.JmxManager;
import com.harmony.umbrella.util.StringUtils;

/**
 * JavaEE的应用上下文实现
 *
 * @author wuxii@foxmail.com
 */
public class EJBApplicationContext extends ApplicationContext implements EJBContextMBean {

    /**
     * javaEE环境standby
     */
    protected static final int STANDBY = 1;
    /**
     * javaEE环境running
     */
    protected static final int RUNNING = 2;
    /**
     * javaEE环境shutdown
     */
    protected static final int SHUTDOWN = 3;

    private static EJBApplicationContext INSTANCE;

    /**
     * EJB application 属性配置文件地址
     */
    private final URL containerConfigurationUrl;

    private final Properties containerProperties = new Properties();

    /**
     * JMX管理
     */
    private JmxManager jmxManager = JmxManager.getInstance();

    /**
     * 存放与class对应的Context的bean信息，sessionBean信息中包含jndi, bean definition,
     * 和一个被缓存着的实例(可以做单例使用)
     */
    private Map<String, SessionBean> sessionBeanCacheMap = new ConcurrentHashMap<String, SessionBean>();

    private ContextResolver contextResolver;

    private int lifeCycle = STANDBY;

    private EJBApplicationContext(URL url) {
        this.containerConfigurationUrl = url;
    }

    private EJBApplicationContext(Properties props) {
        this.containerProperties.putAll(props);
        this.containerConfigurationUrl = null;
    }

    public static EJBApplicationContext getInstance() {
        return getInstance(new Properties());
    }

    public static EJBApplicationContext getInstance(Properties props) {
        if (INSTANCE == null) {
            synchronized (EJBApplicationContext.class) {
                if (INSTANCE == null) {
                    INSTANCE = new EJBApplicationContext(props);
                    INSTANCE.init();
                }
            }
        }
        return INSTANCE;
    }

    public static EJBApplicationContext getInstance(URL url) {
        if (INSTANCE == null) {
            synchronized (EJBApplicationContext.class) {
                if (INSTANCE == null) {
                    INSTANCE = new EJBApplicationContext(url);
                    INSTANCE.init();
                }
            }
        }
        return INSTANCE;
    }

    /*
     * 初始化加载属性文件，给应用注册JMX
     */
    public void init() {
        if (lifeCycle == STANDBY) {
            synchronized (EJBApplicationContext.class) {
                if (lifeCycle == STANDBY) {

                    // 初始化容器相关属性
                    initProps();

                    // 初始化容器解决策略
                    initResolver();

                    // init database information
                    initDB();

                    // 初始化JMX
                    initJMX();

                    LOG.debug("init ejb application success");
                    lifeCycle = RUNNING;
                }
            }
        }
    }

    private void initProps() {
        Properties properties = new Properties();
        // 1 application properties
        properties.putAll(applicationProperties);
        // 2 configuration properties
        if (containerConfigurationUrl != null) {
            InputStream is = null;
            try {
                is = containerConfigurationUrl.openStream();
                properties.load(is);
            } catch (IOException e) {
                LOG.warn("not found custom container properties file, use default");
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
        // 3 user input properties
        properties.putAll(containerProperties);
        // reset
        this.containerProperties.putAll(properties);
    }

    private void initResolver() {
        this.contextResolver = createContextResolver(getInformationOfServer(), this.containerProperties);
    }

    private void initJMX() {
        if (jmxManager.isRegistered(EJBContext.class)) {
            jmxManager.unregisterMBean(EJBContext.class);
        }
        // register jmx
        jmxManager.registerMBean(new EJBContext(this));
    }

    private void initDB() {
        String name = containerProperties.getProperty("application.datasource.name");
        if (StringUtils.isNotBlank(name)) {
            try {
                DataSource ds = (DataSource) lookup(name);
                initializeDBInformation(ds.getConnection(), true);
            } catch (Exception e) {
                LOG.error("init database information failed, {}", e.toString());
            }
        }
    }

    /**
     * 根据jndi名称获取JavaEE环境中指定的对象
     *
     * @param jndi
     *            jndi名称
     * @param <T>
     *            需要查找的类型
     * @return 指定jndi的bean
     * @throws ClassCastException
     *             找到的bean类型不为requireType
     * @throws ApplicationContextException
     *             不存在指定的jndi
     */
    @SuppressWarnings("unchecked")
    public <T> T lookup(String jndi) throws ApplicationContextException {
        try {
            return (T) getContext().lookup(jndi);
        } catch (NamingException e) {
            LOG.warn("jndi [{}] not find", jndi);
            throw new ApplicationContextException(e.getMessage(), e);
        }
    }

    /**
     * 从JavaEE环境中获取指定类实例
     * <p/>
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
     * 从JavaEE环境中获取指定类实例
     * <p/>
     * <li>首先根据所运行的容器不同通过 {@linkplain BeanResolver} 将 {@code clazz} 格式化为特定的
     * {@code jndi}
     * <li>如果格式化后的jndi名称未找到对应类型的bean， 则通过 {@linkplain ContextResolver}
     * 递归读取上下文查找需要指定的内容
     * <li>若以上方式均为找到则返回{@code null}
     *
     * @param clazz
     *            待查找的类
     * @param mappedName
     *            bean的映射名称
     * @return 指定类型的bean
     */
    @SuppressWarnings("unchecked")
    public <T> T lookup(Class<T> clazz, String mappedName) {
        return (T) lookup(new BeanDefinition(clazz, mappedName));
    }

    public Object lookup(BeanDefinition bd) {
        Object bean = getFromCacheIfAccessible(bd);
        if (bean == null) {
            synchronized (sessionBeanCacheMap) {
                bean = getFromCacheIfAccessible(bd);
                if (bean == null) {
                    SessionBean sessionBean = contextResolver.search(bd, getContext());
                    validState();
                    if (sessionBean != null) {
                        LOG.info("lookup {} by jndi [{}]", bd.getBeanClass().getName(), sessionBean.getJndi());
                        putIntoCacheIfAbsent(bd, sessionBean);
                        return sessionBean.getBean();
                    }
                }
            }
        }
        LOG.warn("can't lookup {}", bd.getBeanClass().getName());
        return null;
    }

    private Object getFromCacheIfAccessible(BeanDefinition bd) {
        String key = cacheMappedKey(bd.getBeanClass(), bd.getMappedName());
        SessionBean sessionBean = sessionBeanCacheMap.get(key);
        if (sessionBean != null) {
            LOG.debug("lookup bean[{}] use cached session bean {}", sessionBean.getJndi(), sessionBean);
            if (sessionBean.isCacheable()) {
                return sessionBean.getBean();
            } else {
                try {
                    return lookup(sessionBean.getJndi());
                } catch (Exception e) {
                    sessionBeanCacheMap.remove(key);
                }
            }
        }
        return null;
    }

    private void putIntoCacheIfAbsent(BeanDefinition bd, SessionBean sessionBean) {
        String key = cacheMappedKey(bd.getBeanClass(), bd.getMappedName());
        if (!sessionBeanCacheMap.containsKey(key)) {
            sessionBeanCacheMap.put(key, sessionBean);
        }
    }

    /**
     * 初始化客户端JavaEE环境
     *
     * @return {@linkplain javax.naming.Context}
     * @throws ApplicationContextException
     *             初始化上下文失败
     */
    public Context getContext() {
        try {
            return new InitialContext(containerProperties);
        } catch (NamingException e) {
            throw new ApplicationContextException(e.getMessage(), e.getCause());
        }
    }

    public void setContextResolver(ContextResolver contextResolver) {
        this.contextResolver = contextResolver;
    }

    private void validState() {
        if (lifeCycle != RUNNING) {
            throw new IllegalStateException("application context not init");
        }
    }

    private String cacheMappedKey(Class<?> clazz, String mappedName) {
        return new StringBuilder(clazz.getName()).append(".")//
                .append(StringUtils.isBlank(mappedName) ? "" : mappedName)//
                .toString();
    }

    // ################### BeanFactory #################

    @Override
    public <T> T getBean(String beanName) throws NoSuchBeanFoundException {
        return getBean(beanName, BeanFactory.PROTOTYPE);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getBean(String beanName, String scope) throws NoSuchBeanFoundException {
        Exception ex = null;
        Object bean = null;
        try {
            bean = lookup(beanName);
        } catch (ApplicationContextException e) {
            ex = e;
            try {
                bean = getBean(ClassUtils.forName(beanName), scope);
            } catch (ClassNotFoundException e1) {
                ex.addSuppressed(e1);
            }
        }
        if (bean == null) {
            throw new NoSuchBeanFoundException(beanName + " not find!", ex);
        }
        return (T) bean;
    }

    @Override
    public <T> T getBean(Class<T> beanClass) throws NoSuchBeanFoundException {
        return getBean(beanClass, BeanFactory.PROTOTYPE);
    }

    @Override
    public <T> T getBean(Class<T> beanClass, String scope) throws NoSuchBeanFoundException {
        T bean = null;
        try {
            bean = lookup(beanClass);
        } catch (ApplicationContextException e) {
            throw new NoSuchBeanFoundException(beanClass + " not find!", e);
        }
        if (bean == null) {
            throw new NoSuchBeanFoundException(beanClass + " not find!");
        }
        return bean;
    }

    // ############## JMX ############

    public void setContainerProperties(Properties containerProperties) {
        this.containerProperties.clear();
        this.containerProperties.putAll(containerProperties);
    }

    public void addContainerProperties(Properties containerProperties) {
        this.containerProperties.putAll(containerProperties);
    }

    /*
     * JMX method
     */
    @Override
    public void loadProperties() {
        LOG.info("reload properties {}", containerConfigurationUrl);
    }

    @Override
    public String showProperties() {
        return containerProperties.toString();
    }

    @Override
    public String jndiPropertiesFileLocation() {
        return containerConfigurationUrl != null ? containerConfigurationUrl.toString() : null;
    }

    /*
     * JMX method
     */
    @Override
    public void clearProperties() {
        containerProperties.clear();
        LOG.info("clear properties");
    }

    /*
     * JMX method
     */
    @Override
    public boolean exists(String className) {
        LOG.info("test exists bean {}", className);
        try {
            getBean(className);
        } catch (NoSuchBeanFoundException e) {
            return false;
        }
        return true;
    }

    @Override
    public void destroy() {
        if (lifeCycle == RUNNING) {
            synchronized (EJBApplicationContext.class) {
                if (lifeCycle == RUNNING) {
                    jmxManager.unregisterMBean(EJBContext.class);
                    this.clearProperties();
                    lifeCycle = SHUTDOWN;
                }
            }
        }
    }

    /**
     * 在指定上下文属性中查找对于jndi名称的对象
     *
     * @param jndiName
     *            jndi名称
     * @param jndiProperties
     *            指定上下文属性
     * @return 在环境中对于jndi的bean
     * @throws ApplicationContextException
     *             上下文初始化失败, 或者未找到对应的jndi bean
     */
    public static Object lookup(String jndiName, Properties jndiProperties) throws ApplicationContextException {
        try {
            return new InitialContext(jndiProperties).lookup(jndiName);
        } catch (NamingException e) {
            throw new ApplicationContextException(e.getMessage(), e.getCause());
        }
    }

}
