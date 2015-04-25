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
import com.harmony.umbrella.util.StringUtils;
import com.harmony.umbrella.util.reflect.MethodUtils;

/**
 * @author wuxii@foxmail.com
 */
public class BeanDefinition {

	@SuppressWarnings("unchecked")
	private static final List<Class<? extends Annotation>> sessionClass = Collections.unmodifiableList(Arrays.asList(Stateless.class, Stateful.class, Singleton.class));

	private final Class<?> beanClass;
	private String mappedName;

	public BeanDefinition(Class<?> beanClass) {
		this.beanClass = beanClass;
		this.mappedName = getMappedName(beanClass);
	}

	public BeanDefinition(Class<?> beanClass, String mappedName) {
		this.beanClass = beanClass;
		this.mappedName = StringUtils.isEmpty(mappedName) ? getMappedName(beanClass) : mappedName;
	}

	public Class<?> getBeanClass() {
		return beanClass;
	}

	public String getDescription() {
		Annotation ann = getSessionBeanAnnotation(beanClass);
		if (ann != null) {
			return (String) MethodUtils.invokeMethod("description", ann);
		}
		return null;
	}

	public String getName() {
		Annotation ann = getSessionBeanAnnotation(beanClass);
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
		return getSessionBeanAnnotation(beanClass) != null;
	}

	public String getMappedName() {
		return mappedName;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((beanClass == null) ? 0 : beanClass.hashCode());
		result = prime * result + ((mappedName == null) ? 0 : mappedName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BeanDefinition other = (BeanDefinition) obj;
		if (beanClass == null) {
			if (other.beanClass != null)
				return false;
		} else if (!beanClass.equals(other.beanClass))
			return false;
		if (mappedName == null) {
			if (other.mappedName != null)
				return false;
		} else if (!mappedName.equals(other.mappedName))
			return false;
		return true;
	}

	public static boolean isRemoteClass(Class<?> clazz) {
		return clazz.isInterface() && clazz.getAnnotation(Remote.class) != null;
	}

	public static boolean isLocalClass(Class<?> clazz) {
		return clazz.isInterface() && clazz.getAnnotation(Local.class) != null;
	}

	public static final String getMappedName(Class<?> clazz) {
		Annotation ann = getSessionBeanAnnotation(clazz);
		if (ann != null) {
			return (String) MethodUtils.invokeMethod("mappedName", ann);
		}
		return null;
	}

	private static Annotation getSessionBeanAnnotation(Class<?> clazz) {
		for (Class<? extends Annotation> sc : sessionClass) {
			Annotation ann = clazz.getAnnotation(sc);
			if (ann != null) {
				return ann;
			}
		}
		return null;
	}

}
