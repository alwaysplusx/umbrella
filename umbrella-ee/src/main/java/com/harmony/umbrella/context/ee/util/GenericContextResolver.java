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
package com.harmony.umbrella.context.ee.util;

import java.util.Properties;

import javax.naming.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.context.ee.BeanDefinition;
import com.harmony.umbrella.context.ee.ContextResolver;
import com.harmony.umbrella.util.StringUtils;

/**
 * 通用的BeanContextResolver
 * 
 * @author wuxii@foxmail.com
 */
public class GenericContextResolver implements ContextResolver {

	protected static final Logger log = LoggerFactory.getLogger(GenericContextResolver.class);

	protected final String beanSuffix;
	protected final String remoteSuffix;
	protected final String localSuffix;
	protected final String beanSeparator;

	public GenericContextResolver(Properties props) {
		this.beanSuffix = props.getProperty("jndi.format.bean", SUFFIX_BEAN);
		this.remoteSuffix = props.getProperty("jndi.format.remote", SUFFIX_REMOTE);
		this.localSuffix = props.getProperty("jndi.format.local", SUFFIX_LOCAL);
		this.beanSeparator = props.getProperty("jndi.format.sparator", BEAN_JNDI_SEPARATOR);
	}

	@Override
	public String resolveBeanName(BeanDefinition beanDefinition) {
		if (beanDefinition.isSessionBean()) {
			return resolveSessionBeanName(beanDefinition.getMappedName(), beanDefinition.getBeanClass(), beanDefinition.getSuitableRemoteClass());
		} else if (beanDefinition.isRemoteClass()) {
			return resolveRemoteClassBeanName(beanDefinition.getMappedName(), beanDefinition.getSuitableRemoteClass());
		} else if (beanDefinition.isLocalClass()) {
			return resolveLocalClassBeanName(beanDefinition.getMappedName(), beanDefinition.getSuitableLocalClass());
		}
		throw new RuntimeException("unsupport bean definition");
	}

	@Override
	public boolean isDeclareBean(BeanDefinition declaer, Object bean) {
		if (isWrappedBean(bean)) {
			bean = unwrap(bean);
		}
		if (StringUtils.isNotBlank(declaer.getMappedName())) {
			if (!isSameMappedName(declaer, bean.getClass())) {
				return false;
			}
		}
		Class<?> beanClass = declaer.getBeanClass();
		Class<?> suitableRemoteClass = declaer.getSuitableRemoteClass();
		return beanClass.isInstance(bean) || (suitableRemoteClass != null && suitableRemoteClass.isInstance(bean));
	}

	private boolean isSameMappedName(BeanDefinition declaer, Class<?> beanClass) {
		return true;
	}

	protected String resolveSessionBeanName(String mappedName, Class<?> beanClass, Class<?> remoteClass) {
		if (StringUtils.isEmpty(mappedName)) {
			mappedName = beanClass.getSimpleName();
		}
		if (remoteClass == null) {
			remoteClass = beanClass;
		}
		String jndi = mappedName + beanSeparator + remoteClass.getName();
		log.info("resolve [{{}} -> {{}}]", beanClass.getName(), jndi);
		return jndi;
	}

	protected String resolveRemoteClassBeanName(String mappedName, Class<?> remoteClass) {
		if (StringUtils.isEmpty(mappedName)) {
			String remoteClassName = remoteClass.getSimpleName();
			int remoteIndex = remoteClassName.lastIndexOf(remoteSuffix);
			if (remoteIndex > 0) {
				mappedName = remoteClassName.substring(0, remoteIndex) + beanSuffix;
			}
		}
		String jndi = mappedName + beanSeparator + remoteClass.getName();
		log.info("resolve [{{}} -> {{}}]", remoteClass.getName(), jndi);
		return jndi;
	}

	protected String resolveLocalClassBeanName(String mappedName, Class<?> localClass) {
		if (mappedName == null) {
			String remoteClassName = localClass.getSimpleName();
			int remoteIndex = remoteClassName.lastIndexOf(localSuffix);
			if (remoteIndex > 0) {
				mappedName = remoteClassName.substring(0, remoteIndex) + beanSuffix;
			}
		}
		String jndi = mappedName + beanSeparator + localClass.getName();
		log.info("resolve [{{}} -> {{}}]", localClass.getName(), jndi);
		return jndi;
	}

	protected boolean isWrappedBean(Object object) {
		return false;
	}

	protected boolean isContext(Object object) {
		return object instanceof Context;
	}

	@Override
	public Object unwrap(Object bean) {
		return bean;
	}

}
