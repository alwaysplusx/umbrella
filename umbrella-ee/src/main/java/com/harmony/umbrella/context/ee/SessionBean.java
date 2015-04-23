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
package com.harmony.umbrella.context.ee;

/**
 * @author wuxii@foxmail.com
 */
public class SessionBean {

	protected final String jndi;
	protected final BeanDefinition beanDefinition;
	protected final Object bean;

	public SessionBean(String jndi, BeanDefinition beanDefinition, Object bean) {
		this.jndi = jndi;
		this.beanDefinition = beanDefinition;
		this.bean = bean;
	}

	public BeanDefinition getBeanDefinition() {
		return beanDefinition;
	}

	public boolean isCacheable() {
		return beanDefinition.isSessionBean();
	}

	@SuppressWarnings("unchecked")
	public <T> T getCachedBean() {
		return (T) bean;
	}

	public String getJndi() {
		return jndi;
	}

}