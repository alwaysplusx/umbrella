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

import java.util.Properties;

/**
 * @author wuxii@foxmail.com
 */
public class BeanJndiNameFormat implements JndiNameFormat {

	protected final String beanSuffix;
	protected final String remoteSuffix;
	protected final String localSuffix;

	public BeanJndiNameFormat() {
		this(new Properties());
	}

	public BeanJndiNameFormat(Properties props) {
		this.beanSuffix = props.getProperty(PROP_KEY_BEAN, SUFFIX_BEAN);
		this.remoteSuffix = props.getProperty(PROP_KEY_REMOTE, SUFFIX_REMOTE);
		this.localSuffix = props.getProperty(PROP_KEY_LOCAL, SUFFIX_LOCAL);
	}

	@Override
	public String format(BeanDefinition beanDefinition) {
		if (beanDefinition.isSessionBean()) {
			return formatSessionBean(beanDefinition);
		} else if (beanDefinition.isRemoteClass()) {
			return formatRemoteClass(beanDefinition);
		} else if (beanDefinition.isLocalClass()) {
			return formatLocalClass(beanDefinition);
		}
		throw new RuntimeException("unsupport bean definition");
	}

	protected String formatSessionBean(BeanDefinition beanDefinition) {
		String mappedName = beanDefinition.getMappedName();
		if (mappedName == null) {
			mappedName = beanDefinition.getBeanClass().getSimpleName();
		}
		Class<?> remoteClass = beanDefinition.getSuitableRemoteClass();
		if (remoteClass == null) {
			remoteClass = beanDefinition.getBeanClass();
		}
		return mappedName + "#" + remoteClass.getName();
	}

	protected String formatRemoteClass(BeanDefinition beanDefinition) {
		String className = beanDefinition.getBeanClass().getSimpleName();
		int remoteIndex = className.lastIndexOf(remoteSuffix);
		if (remoteIndex > -1) {
			className = className.substring(0, remoteIndex) + beanSuffix;
		}
		return className + "#" + beanDefinition.getBeanClass().getName();
	}

	protected String formatLocalClass(BeanDefinition beanDefinition) {
		String className = beanDefinition.getBeanClass().getSimpleName();
		int localIndex = className.lastIndexOf(localSuffix);
		if (localIndex > -1) {
			className = className.substring(0, localIndex);
		}
		return className + beanSuffix + "#" + beanDefinition.getBeanClass().getPackage().getName() + "." + className + remoteSuffix;
	}

}