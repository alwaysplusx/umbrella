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
package com.harmony.umbrella.context.spring;

import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.harmony.umbrella.context.ApplicationContext;
import com.harmony.umbrella.core.NoSuchBeanFindException;

/**
 * @author wuxii@foxmail.com
 */
public class SpringApplicationContext extends ApplicationContext implements BeanFactory {

	public static final String SPRING_XMLCONTENT_FILE_LOCATION = "applicationContext.xml";

	private BeanFactory beanFactory;

	private static SpringApplicationContext instance;

	private SpringApplicationContext() {
		this(new Properties());
	}

	private SpringApplicationContext(Properties props) {
		this.beanFactory = new ClassPathXmlApplicationContext(props.getProperty("application.file.location", SPRING_XMLCONTENT_FILE_LOCATION));
	}

	public static SpringApplicationContext getInstance() {
		return getInstance(new Properties());
	}

	public static SpringApplicationContext getInstance(Properties props) {
		if (instance == null) {
			synchronized (SpringApplicationContext.class) {
				if (instance == null) {
					instance = new SpringApplicationContext(props);
				}
			}
		}
		return instance;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getBean(String beanName, String scope) throws NoSuchBeanFindException {
		return (T) beanFactory.getBean(beanName);
	}

	@Override
	public <T> T getBean(Class<T> beanClass) throws NoSuchBeanFindException {
		return beanFactory.getBean(beanClass);
	}

	@Override
	public <T> T getBean(Class<T> beanClass, String scope) throws NoSuchBeanFindException {
		return beanFactory.getBean(beanClass);
	}

	@Override
	public void init() {
	}

	@Override
	public void destory() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getBean(String name) throws BeansException {
		return beanFactory.getBean(name);
	}

	@Override
	public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
		return beanFactory.getBean(name, requiredType);
	}

	@Override
	public Object getBean(String name, Object... args) throws BeansException {
		return beanFactory.getBean(name, args);
	}

	@Override
	public <T> T getBean(Class<T> requiredType, Object... args) throws BeansException {
		return beanFactory.getBean(requiredType, args);
	}

	@Override
	public boolean containsBean(String name) {
		return beanFactory.containsBean(name);
	}

	@Override
	public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
		return beanFactory.isSingleton(name);
	}

	@Override
	public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
		return beanFactory.isPrototype(name);
	}

	@Override
	public boolean isTypeMatch(String name, Class<?> targetType) throws NoSuchBeanDefinitionException {
		return beanFactory.isTypeMatch(name, targetType);
	}

	@Override
	public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
		return beanFactory.getType(name);
	}

	@Override
	public String[] getAliases(String name) {
		return beanFactory.getAliases(name);
	}

}
