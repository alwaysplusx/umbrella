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
package com.harmony.modules.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 通过类的反射{@linkplain Class#newInstance()}来创建Bean
 * @author wuxii@foxmail.com
 */
public class ClassBeanLoader implements BeanLoader, Serializable {

	private static final long serialVersionUID = 1L;
	private Map<Class<?>, Object> beans = new HashMap<Class<?>, Object>();

	@SuppressWarnings("unchecked")
	@Override
	public <T> T loadBean(Class<T> beanClass) {
		if (!beans.containsKey(beanClass)) {
			beans.put(beanClass, newBean(beanClass, null));
		}
		return (T) beans.get(beanClass);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T loadBean(Class<T> beanClass, String scope) {
		if (SINGLETON.equals(scope)) {
			return loadBean(beanClass);
		} else if (PROTOTYPE.equals(scope)) {
			return (T) newBean(beanClass, null);
		} else {
			throw new IllegalArgumentException("unsupport scope " + scope);
		}
	}

	private Object newBean(Class<?> beanClass, Map<String, Object> properties) {
		try {
			return beanClass.newInstance();
		} catch (InstantiationException e) {
			throw new IllegalArgumentException(e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException(e);
		}
	}
}