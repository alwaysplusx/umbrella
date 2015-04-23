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

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Singleton;
import javax.ejb.Stateful;
import javax.ejb.Stateless;

import com.harmony.umbrella.util.ClassUtils;
import com.harmony.umbrella.util.reflect.MethodUtils;

/**
 * @author wuxii@foxmail.com
 */
public class BeanDefinition {

	@SuppressWarnings("unchecked")
	private static final List<Class<? extends Annotation>> sessionClass = Collections.unmodifiableList(Arrays.asList(Stateless.class, Stateful.class, Singleton.class));

	private final Class<?> beanClass;

	public BeanDefinition(Class<?> beanClass) {
		this.beanClass = beanClass;
	}

	public Class<?> getBeanClass() {
		return beanClass;
	}

	public String getDescription() {
		Annotation ann = getSessionBeanAnnotation();
		if (ann != null) {
			return (String) MethodUtils.invokeMethod("description", ann);
		}
		return null;
	}

	public String getName() {
		Annotation ann = getSessionBeanAnnotation();
		if (ann != null) {
			return (String) MethodUtils.invokeMethod("name", ann);
		}
		return null;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isSessionBean() {
		return getSessionBeanAnnotation() != null;
	}

	public String getMappedName() {
		Annotation ann = getSessionBeanAnnotation();
		if (ann != null) {
			return (String) MethodUtils.invokeMethod("mappedName", ann);
		}
		return null;
	}

	public boolean isRemoteClass() {
		return beanClass.isInterface() && beanClass.getAnnotation(Remote.class) != null;
	}

	public boolean isLocalClass() {
		return beanClass.isInterface() && beanClass.getAnnotation(Local.class) != null;
	}

	public Class<?> getSuitableRemoteClass() {
		Class<?>[] classes = getRemoteClass();
		return classes.length > 0 ? classes[0] : null;
	}

	public Class<?> getSuitableLocalClass() {
		Class<?>[] classes = getLocalClass();
		return classes.length > 0 ? classes[0] : null;
	}

	@SuppressWarnings("rawtypes")
	public Class<?>[] getRemoteClass() {
		Set<Class> result = new HashSet<Class>();
		Remote ann = beanClass.getAnnotation(Remote.class);
		if (isRemoteClass()) {
			result.add(beanClass);
			Collections.addAll(result, ann.value());
			return result.toArray(new Class[result.size()]);
		}
		for (Class clazz : ClassUtils.getAllInterfaces(beanClass)) {
			if (isRemoteClass(clazz)) {
				result.add(clazz);
			}
		}
		return result.toArray(new Class[result.size()]);
	}

	@SuppressWarnings("rawtypes")
	public Class<?>[] getLocalClass() {
		Set<Class> result = new HashSet<Class>();
		Local ann = beanClass.getAnnotation(Local.class);
		if (isLocalClass()) {
			result.add(beanClass);
			Collections.addAll(result, ann.value());
			return result.toArray(new Class[result.size()]);
		}
		for (Class clazz : ClassUtils.getAllInterfaces(beanClass)) {
			if (isLocalClass(clazz)) {
				result.add(clazz);
			}
		}
		return result.toArray(new Class[result.size()]);
	}

	public boolean hasRemoteClass() {
		return getRemoteClass() != null && getRemoteClass().length > 0;
	}

	public boolean hasLocalClass() {
		return getLocalClass() != null && getLocalClass().length > 0;
	}

	public boolean isStateless() {
		return beanClass.getAnnotation(Stateless.class) != null;
	}

	public boolean isStateful() {
		return beanClass.getAnnotation(Stateful.class) != null;
	}

	public boolean isSingleton() {
		return beanClass.getAnnotation(Singleton.class) != null;
	}

	private Annotation getSessionBeanAnnotation() {
		for (Class<? extends Annotation> clazz : sessionClass) {
			Annotation ann = beanClass.getAnnotation(clazz);
			if (ann != null) {
				return ann;
			}
		}
		return null;
	}

	public static boolean isRemoteClass(Class<?> clazz) {
		return clazz.isInterface() && clazz.getAnnotation(Remote.class) != null;
	}

	public static boolean isLocalClass(Class<?> clazz) {
		return clazz.isInterface() && clazz.getAnnotation(Local.class) != null;
	}

}
