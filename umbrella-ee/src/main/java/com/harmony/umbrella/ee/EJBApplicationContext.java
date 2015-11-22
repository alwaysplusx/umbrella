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
package com.harmony.umbrella.ee;

import static com.harmony.umbrella.ee.ResolverManager.*;

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
import com.harmony.umbrella.core.BeanFactory;
import com.harmony.umbrella.core.NoSuchBeanFindException;
import com.harmony.umbrella.ee.jmx.EJBContext;
import com.harmony.umbrella.ee.jmx.EJBContextMBean;
import com.harmony.umbrella.util.ClassUtils;
import com.harmony.umbrella.util.JmxManager;
import com.harmony.umbrella.util.PropUtils;
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

    /**
     * EJB application 属性配置文件地址
     */
    private final String jndiPropertiesFileLocation;

    /**
     * JMX管理
     */
    private JmxManager jmxManager = JmxManager.getInstance();

    /**
     * 存放与class对应的Context的bean信息，sessionBean信息中包含jndi, bean definition, 和一个被缓存着的实例(可以做单例使用)
     */
    private Map<String, SessionBean> sessionBeanMap = new ConcurrentHashMap<String, SessionBean>();

    private static EJBApplicationContext INSTANCE;

    private ContextResolver contextResolver;

    private int lifeCycle = STANDBY;

    private EJBApplicationContext(Properties props) {
        this.jndiPropertiesFileLocation = props.getProperty("jndi.properties.file", APPLICATION_PROPERTIES_LOCATION);
        this.applyContextProperties(props);
        this.contextResolver = createContextResolver(getInformationOfServer(), contextProperties);
    }

    public static EJBApplicationContext getInstance() {
        return getInstance(null);
    }

    public static EJBApplicationContext getInstance(Properties props) {
        if (INSTANCE == null) {
            synchronized (EJBApplicationContext.class) {
                if (INSTANCE == null) {
                    INSTANCE = new EJBApplicationContext(props == null ? new Properties() : props);
                    INSTANCE.init();
                } else if (props != null && !props.isEmpty()) {
                    INSTANCE.applyContextProperties(props);
                }
            }
        }
        return INSTANCE;
    }

    /*
     * 初始化加载属性文件，给应用注册JMX
     * 
     * @see com.harmony.umbrella.context.ApplicationContext#init()
     */
    public void init() {
        if (lifeCycle == STANDBY) {
            synchronized (EJBApplicationContext.class) {
                if (lifeCycle == STANDBY) {

                    if (jmxManager.isRegistered(EJBContext.class)) {
                        jmxManager.unregisterMBean(EJBContext.class);
                    }
                    // register jmx
                    jmxManager.registerMBean(new EJBContext(this));

                    // init database information
                    initDB();

                    LOG.debug("init ejb application success");
                    lifeCycle = RUNNING;
                }
            }
        }
    }

    private void initDB() {
        String name = contextProperties.getProperty("application.datasource.name");
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
     *         jndi名称
     * @param <T>
     *         需要查找的类型
     * @return 指定jndi的bean
     * @throws ClassCastException
     *         找到的bean类型不为requireType
     * @throws ApplicationContextException
     *         不存在指定的jndi
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
     * <li>首先根据类型将解析clazz对应的jndi名称 <li>如果解析的jndi名称未找到对应类型的bean， 则通过递归 {@linkplain javax.naming.Context} 中的内容查找
     * <li>若以上方式均为找到则返回{@code null}
     *
     * @param clazz
     *         待查找的类
     * @return 指定类型的bean, 如果为找到返回{@code null}
     * @throws ApplicationContextException
     *         初始化JavaEE环境失败
     */
    public <T> T lookup(Class<T> clazz) throws ApplicationContextException {
        return lookup(clazz, null);
    }

    /**
     * 从JavaEE环境中获取指定类实例
     * <p/>
     * <li>首先根据所运行的容器不同通过 {@linkplain BeanResolver} 将 {@code clazz} 格式化为特定的 {@code jndi} <li>如果格式化后的jndi名称未找到对应类型的bean，
     * 则通过 {@linkplain ContextResolver} 递归读取上下文查找需要指定的内容 <li>若以上方式均为找到则返回{@code null}
     *
     * @param clazz
     *         待查找的类
     * @param mappedName
     *         bean的映射名称
     * @return 指定类型的bean
     */
    @SuppressWarnings("unchecked")
    public <T> T lookup(Class<T> clazz, String mappedName) {
        String key = sessionKey(clazz, mappedName);
        SessionBean sessionBean = sessionBeanMap.get(key);
        if (sessionBean != null) {
            LOG.debug("lookup bean[{}] use cached session bean {}", sessionBean.getJndi(), sessionBean);
            if (sessionBean.isCacheable()) {
                return (T) sessionBean.getBean();
            } else {
                try {
                    return (T) lookup(sessionBean.getJndi());
                } catch (Exception e) {
                    sessionBeanMap.remove(key);
                }
            }
        }

        synchronized (sessionBeanMap) {
            sessionBean = sessionBeanMap.get(key);
            if (sessionBean == null) {
                sessionBean = contextResolver.search(new BeanDefinition(clazz, mappedName), getContext());
                if (sessionBean != null) {
                    LOG.info("lookup {} by jndi [{}]", clazz.getName(), sessionBean.getJndi());
                    sessionBeanMap.put(key, sessionBean);
                } else {
                    LOG.warn("can't lookup {}", clazz.getName());
                }
            }
        }

        return (T) (sessionBean != null ? sessionBean.getBean() : null);
    }

    private String sessionKey(Class<?> clazz, String mappedName) {
        return new StringBuilder(clazz.getName()).append(".")//
                .append(StringUtils.isBlank(mappedName) ? "" : mappedName)//
                .toString();
    }

    /**
     * 初始化客户端JavaEE环境
     *
     * @return {@linkplain javax.naming.Context}
     * @throws ApplicationContextException
     *         初始化上下文失败
     */
    public Context getContext() {
        try {
            return new InitialContext(contextProperties);
        } catch (NamingException e) {
            throw new ApplicationContextException(e.getMessage(), e.getCause());
        }
    }

    /**
     * 在指定上下文属性中查找对于jndi名称的对象
     *
     * @param jndiName
     *         jndi名称
     * @param jndiProperties
     *         指定上下文属性
     * @return 在环境中对于jndi的bean
     * @throws ApplicationContextException
     *         上下文初始化失败, 或者未找到对应的jndi bean
     */
    public static Object lookup(String jndiName, Properties jndiProperties) throws ApplicationContextException {
        try {
            return new InitialContext(jndiProperties).lookup(jndiName);
        } catch (NamingException e) {
            throw new ApplicationContextException(e.getMessage(), e.getCause());
        }
    }

    /**
     * 使用传入的属性覆盖已经存在的环境Application properties
     *
     * @param props
     *         传入的属性
     */
    protected void applyContextProperties(Properties props) {
        if (PropUtils.exists(jndiPropertiesFileLocation)) {
            this.contextProperties.putAll(PropUtils.loadProperties(jndiPropertiesFileLocation));
        } else {
            LOG.warn("properties file [{}] is not exists", jndiPropertiesFileLocation);
        }

        if (props != null && !props.isEmpty()) {
            this.contextProperties.putAll(props);
        }
    }

    public void setContextResolver(ContextResolver contextResolver) {
        this.contextResolver = contextResolver;
    }

    // ################### BeanFactory #################

    @Override
    public <T> T getBean(String beanName) throws NoSuchBeanFindException {
        return getBean(beanName, BeanFactory.PROTOTYPE);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getBean(String beanName, String scope) throws NoSuchBeanFindException {
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
            throw new NoSuchBeanFindException(beanName + " not find!", ex);
        }
        return (T) bean;
    }

    @Override
    public <T> T getBean(Class<T> beanClass) throws NoSuchBeanFindException {
        return getBean(beanClass, BeanFactory.PROTOTYPE);
    }

    @Override
    public <T> T getBean(Class<T> beanClass, String scope) throws NoSuchBeanFindException {
        T bean = null;
        try {
            bean = lookup(beanClass);
        } catch (ApplicationContextException e) {
            throw new NoSuchBeanFindException(beanClass + " not find!", e);
        }
        if (bean == null) {
            throw new NoSuchBeanFindException(beanClass + " not find!");
        }
        return bean;
    }

    // ############## JMX ############
    /*
     * JMX method
     */
    @Override
    public void loadProperties() {
        LOG.info("reload properties {}", jndiPropertiesFileLocation);
        this.applyContextProperties(null);
    }

    /*
     * JMX method
     */
    @Override
    public void clearProperties() {
        contextProperties.clear();
        LOG.info("clear properties");
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
        return url.toString();
    }

    /*
     * JMX method
     */
    @Override
    public boolean exists(String className) {
        LOG.info("test exists bean {}", className);
        try {
            getBean(className);
        } catch (NoSuchBeanFindException e) {
            return false;
        }
        return true;
    }

    /*
     * JMX method
     */
    @Override
    public void clear() {
        sessionBeanMap.clear();
        contextResolver.clear();
        LOG.info("clear cached context property");
    }

    @Override
    public void clear(String jndi) {
        sessionBeanMap.remove(jndi);
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

}
