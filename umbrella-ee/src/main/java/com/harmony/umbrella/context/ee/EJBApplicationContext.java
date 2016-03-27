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

import javax.ejb.EJB;

import com.harmony.umbrella.context.ApplicationContext;
import com.harmony.umbrella.context.ee.jmx.EJBContextMBean;
import com.harmony.umbrella.core.BeansException;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.util.PropUtils;

import java.io.IOException;
import java.util.Properties;

/**
 * JavaEE的应用上下文实现
 *
 * @author wuxii@foxmail.com
 */
public class EJBApplicationContext extends ApplicationContext implements EJBContextMBean, EJBBeanFactory {

    public static final String CONTEXT_PROPERTIES_FILE_LOCATION = "jndi.properties";

    private static final Log log = Logs.getLog(EJBApplicationContext.class);

    private String contextPropertiesFileLocation;
    private EJBBeanFactory beanFactory;

    public static EJBApplicationContext create() {
        return create(new Properties());
    }

    public static EJBApplicationContext create(Properties properties) {
        return new EJBApplicationContext(EJBBeanFactoryImpl.create(properties));
    }

    public static EJBApplicationContext create(EJBBeanFactory beanFactory, String contextPropertiesFileLocation) {
        return new EJBApplicationContext(beanFactory, contextPropertiesFileLocation);
    }

    public EJBApplicationContext(EJBBeanFactory beanFactory) {
        this(beanFactory, CONTEXT_PROPERTIES_FILE_LOCATION);
    }

    public EJBApplicationContext(EJBBeanFactory beanFactory, String contextPropertiesFileLocation) {
        this.beanFactory = beanFactory;
        this.contextPropertiesFileLocation = contextPropertiesFileLocation;
    }

    @Override
    public void init() {
    }

    @Override
    public void destroy() {
    }

    @Override
    public <T> T getBean(String beanName) throws BeansException {
        return beanFactory.getBean(beanName);
    }

    @Override
    public <T> T getBean(String beanName, String scope) throws BeansException {
        return beanFactory.getBean(beanName, scope);
    }

    @Override
    public <T> T getBean(Class<T> beanClass) throws BeansException {
        return beanFactory.getBean(beanClass);
    }

    @Override
    public <T> T getBean(Class<T> beanClass, String scope) throws BeansException {
        return beanFactory.getBean(beanClass, scope);
    }

    @Override
    public <T> Object lookup(String jndi) throws BeansException {
        return beanFactory.lookup(jndi);
    }

    @Override
    public <T> T lookup(Class<T> clazz) throws BeansException {
        return beanFactory.lookup(clazz);
    }

    @Override
    public <T> T lookup(Class<T> clazz, EJB ejbAnnotation) throws BeansException {
        return beanFactory.lookup(clazz, ejbAnnotation);
    }

    @Override
    public <T> T lookup(BeanDefinition beanDefinition) throws BeansException {
        return beanFactory.lookup(beanDefinition);
    }

    @Override
    public <T> T lookup(BeanDefinition beanDefinition, EJB ejbAnnotation) throws BeansException {
        return beanFactory.lookup(beanDefinition, ejbAnnotation);
    }

    @Override
    public void setContextProperties(Properties properties) {
        beanFactory.setContextProperties(properties);
    }

    @Override
    public void resetProperties() {
        try {
            beanFactory.setContextProperties(PropUtils.loadProperties(contextPropertiesFileLocation));
        } catch (IOException e) {
            log.error("cannot reset context properties", e);
        }
    }

    @Override
    public boolean exists(String className) {
        Object bean = getBean(className);
        if (bean != null) {
            return true;
        }
        try {
            return getBean(Class.forName(className)) != null;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public String propertiesFileLocation() {
        return contextPropertiesFileLocation;
    }

    @Override
    public String showProperties() {
        try {
            return String.valueOf(PropUtils.loadProperties(contextPropertiesFileLocation));
        } catch (IOException e) {
            return null;
        }
    }

}
