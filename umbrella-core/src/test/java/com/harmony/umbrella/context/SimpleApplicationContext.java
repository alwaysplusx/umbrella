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
package com.harmony.umbrella.context;

import com.harmony.umbrella.core.BeanFactory;
import com.harmony.umbrella.core.NoSuchBeanFindException;
import com.harmony.umbrella.core.SimpleBeanFactory;

/**
 * @author wuxii@foxmail.com
 */
public class SimpleApplicationContext extends ApplicationContext {

	private BeanFactory beanFactory = new SimpleBeanFactory();

	public SimpleApplicationContext() {
	}

	@Override
	public <T> T getBean(String beanName) throws NoSuchBeanFindException {
		return beanFactory.getBean(beanName);
	}

	@Override
	public <T> T getBean(String beanName, String scope) throws NoSuchBeanFindException {
		return beanFactory.getBean(beanName, scope);
	}

	@Override
	public <T> T getBean(Class<T> beanClass) throws NoSuchBeanFindException {
		return beanFactory.getBean(beanClass);
	}

	@Override
	public <T> T getBean(Class<T> beanClass, String scope) throws NoSuchBeanFindException {
		return beanFactory.getBean(beanClass, scope);
	}

	@Override
	public void init() {
	}

	@Override
	public void destory() {
	}

}
