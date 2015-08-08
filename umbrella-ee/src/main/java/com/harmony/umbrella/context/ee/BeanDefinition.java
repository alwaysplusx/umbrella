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
import com.harmony.umbrella.util.MethodUtils;
import com.harmony.umbrella.util.StringUtils;

/**
 * JavaEE {@linkplain Stateless}, {@linkplain Stateful}, {@linkplain Singleton}
 * 为sessionBean
 * <p>
 * 将标记了这些注解的bean的基础信息定义为{@linkplain BeanDefinition}
 * 
 * @author wuxii@foxmail.com
 */
public class BeanDefinition {

    @SuppressWarnings("unchecked")
    private static final List<Class<? extends Annotation>> sessionClasses = Arrays.asList(Stateless.class, Stateful.class, Singleton.class);
    /**
     * beanClass 会话bean的类
     */
    private final Class<?> beanClass;

    /**
     * @see {@linkplain Stateless#mappedName()}
     */
    private String mappedName;

    public BeanDefinition(Class<?> beanClass) {
        this.beanClass = beanClass;
        this.mappedName = getMappedName(beanClass);
    }

    public BeanDefinition(Class<?> beanClass, String mappedName) {
        this.beanClass = beanClass;
        this.mappedName = StringUtils.isEmpty(mappedName) ? getMappedName(beanClass) : mappedName;
    }

    /**
     * bean 类型
     * 
     * @return 被描述的类
     */
    public Class<?> getBeanClass() {
        return beanClass;
    }

    /**
     * @see Stateless#description()
     */
    public String getDescription() {
        Annotation ann = getSessionBeanAnnotation(beanClass);
        if (ann != null) {
            try {
                return (String) MethodUtils.invokeMethod("description", ann);
            } catch (NoSuchMethodException e) {
            }
        }
        return null;
    }

    /**
     * @see Stateless#name()
     */
    public String getName() {
        Annotation ann = getSessionBeanAnnotation(beanClass);
        if (ann != null) {
            try {
                return (String) MethodUtils.invokeMethod("name", ann);
            } catch (NoSuchMethodException e) {
            }
        }
        return null;
    }

    /**
     * 注有{@linkplain Stateless}, {@linkplain Stateful}, {@linkplain Singleton}
     * 三类注解中的一个则为sessionBean
     */
    public boolean isSessionBean() {
        return getSessionBeanAnnotation(beanClass) != null;
    }

    /**
     * @see Stateless#mappedName()
     */
    public String getMappedName() {
        return mappedName;
    }

    /**
     * 是接口并标记了{@linkplain Remote}注解
     */
    public boolean isRemoteClass() {
        return beanClass.isInterface() && beanClass.getAnnotation(Remote.class) != null;
    }

    /**
     * 是接口并标记了{@linkplain Local}注解
     */
    public boolean isLocalClass() {
        return beanClass.isInterface() && beanClass.getAnnotation(Local.class) != null;
    }

    /**
     * 从所有RemoteClass中获取一个最合适的
     */
    public Class<?> getSuitableRemoteClass() {
        Class<?>[] classes = getRemoteClasses();
        return classes.length > 0 ? classes[0] : null;
    }

    /**
     * 从所有LocalClass中获取一个最合适的
     */
    public Class<?> getSuitableLocalClass() {
        Class<?>[] classes = getLocalClasses();
        return classes.length > 0 ? classes[0] : null;
    }

    /**
     * 获取sessionBean中所有的remote class
     */
    @SuppressWarnings("rawtypes")
    public Class<?>[] getRemoteClasses() {
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

    /**
     * 获取sessionBean中所有的local class
     */
    @SuppressWarnings("rawtypes")
    public Class<?>[] getLocalClasses() {
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

    /**
     * session Bean 是否存在{@linkplain Remote}注解的类,或其实现的接口中
     */
    public boolean hasRemoteClass() {
        return getRemoteClasses() != null && getRemoteClasses().length > 0;
    }

    /**
     * session Bean 是否存在{@linkplain Local}注解的类,或其实现的接口中
     */
    public boolean hasLocalClass() {
        return getLocalClasses() != null && getLocalClasses().length > 0;
    }

    /**
     * 标记了{@linkplain Stateless}
     */
    public boolean isStateless() {
        return beanClass.getAnnotation(Stateless.class) != null;
    }

    /**
     * 标记了{@linkplain Stateful}
     * 
     */
    public boolean isStateful() {
        return beanClass.getAnnotation(Stateful.class) != null;
    }

    /**
     * 标记了{@linkplain Singleton}
     * 
     */
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

    /**
     * 工具方法, 判断是否标有{@linkplain Remote}
     */
    public static boolean isRemoteClass(Class<?> clazz) {
        return clazz.isInterface() && clazz.getAnnotation(Remote.class) != null;
    }

    /**
     * 工具方法, 判断是否标有{@linkplain Local}
     */
    public static boolean isLocalClass(Class<?> clazz) {
        return clazz.isInterface() && clazz.getAnnotation(Local.class) != null;
    }

    /**
     * 工具方法, 获取会话bean的mappedName
     */
    public static final String getMappedName(Class<?> clazz) {
        Annotation ann = getSessionBeanAnnotation(clazz);
        if (ann != null) {
            try {
                return (String) MethodUtils.invokeMethod("mappedName", ann);
            } catch (NoSuchMethodException e) {
            }
        }
        return null;
    }

    /**
     * 判断是否为会话bean
     */
    public static boolean isSessionBean(Class<?> clazz) {
        return getSessionBeanAnnotation(clazz) != null;
    }

    private static Annotation getSessionBeanAnnotation(Class<?> clazz) {
        for (Class<? extends Annotation> sc : sessionClasses) {
            Annotation ann = clazz.getAnnotation(sc);
            if (ann != null) {
                return ann;
            }
        }
        return null;
    }

}
