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
package com.harmony.umbrella.context.ee.resolver.wls;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import com.harmony.umbrella.context.ee.util.GenericContextBeanResolver;
import com.harmony.umbrella.context.ee.util.WrappedBeanHandler;

/**
 * @author wuxii@foxmail.com
 */
public class WebLogicContextBeanResolver extends GenericContextBeanResolver {

	private static final Set<WrappedBeanHandler> handlers = new HashSet<WrappedBeanHandler>();

	static {
		handlers.add(new WrappedBeanHandler() {

			@Override
			public Object unwrap(Object bean) {
				try {
					return bean.getClass().getMethod("getBean").invoke(bean);
				} catch (Exception e) {
				}
				return null;
			}

			@Override
			public boolean matches(Class<?> beanClass) {
				try {
					Class<?> clazz = Class.forName("weblogic.ejb.container.internal.SessionEJBContextImpl");
					return clazz.isAssignableFrom(beanClass);
				} catch (ClassNotFoundException e) {
					return false;
				}
			}
		});
	}

	public WebLogicContextBeanResolver(Properties props) {
		super(props);
	}

	@Override
	protected boolean isWrappedBean(Object object) {
		for (WrappedBeanHandler handler : handlers) {
			if (handler.matches(object.getClass())) {
				return true;
			}
		}
		return super.isWrappedBean(object);
	}

	@Override
	public Object unwrap(Object bean) {
		for (WrappedBeanHandler handler : handlers) {
			if (handler.matches(bean.getClass())) {
				return handler.unwrap(bean);
			}
		}
		return super.unwrap(bean);
	}
}
