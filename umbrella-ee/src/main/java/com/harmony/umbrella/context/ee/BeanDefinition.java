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

import static com.harmony.umbrella.context.ee.util.TextMatchCalculator.*;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Singleton;
import javax.ejb.Stateful;
import javax.ejb.Stateless;

import com.harmony.umbrella.util.ClassUtils;
import com.harmony.umbrella.util.ReflectionUtils;
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

    @SuppressWarnings("all")
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
        this(beanClass, getMappedName(beanClass));
    }

    public BeanDefinition(Class<?> beanClass, String mappedName) {
        validBeanClass(beanClass);
        this.beanClass = beanClass;
        this.mappedName = StringUtils.isBlank(mappedName) ? getMappedName(beanClass) : mappedName;
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
        if (isSessionBean()) {
            Annotation ann = getSessionBeanAnnotation(beanClass);
            if (ann != null) {
                try {
                    return (String) ReflectionUtils.invokeMethod("description", ann);
                } catch (NoSuchMethodException e) {
                }
            }
        }
        return null;
    }

    /**
     * @see Stateless#name()
     */
    public String getName() {
        if (isSessionBean()) {
            Annotation ann = getSessionBeanAnnotation(beanClass);
            if (ann != null) {
                try {
                    return (String) ReflectionUtils.invokeMethod("name", ann);
                } catch (NoSuchMethodException e) {
                }
            }
        }
        return null;
    }

    /**
     * @see Stateless#mappedName()
     */
    public String getMappedName() {
        return mappedName;
    }

    /**
     * 注有{@linkplain Stateless}, {@linkplain Stateful}, {@linkplain Singleton}
     * 三类注解中的一个则为sessionBean
     */
    public boolean isSessionBean() {
        return getSessionBeanAnnotation(beanClass) != null;
    }

    /**
     * 是接口标记了{@linkplain Remote}注解, 默认将不是local的接口定义为remote接口
     */
    public boolean isRemoteClass() {
        return beanClass.isInterface() && (beanClass.getAnnotation(Remote.class) != null || !isLocalClass());
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
    @SuppressWarnings("rawtypes")
    public Class<?> getSuitableRemoteClass() {
        List<Class> classes = getRemoteClasses();
        if (isRemoteClass() || isLocalClass() || classes.isEmpty()) {
            return beanClass;
        }
        return findSuitClass(classes);
    }

    /**
     * 从所有LocalClass中获取一个最合适的
     */
    @SuppressWarnings("rawtypes")
    public Class<?> getSuitableLocalClass() {
        List<Class> classes = getLocalClasses();
        if (isLocalClass() || classes.isEmpty()) {
            return beanClass;
        }
        return findSuitClass(classes);
    }

    @SuppressWarnings("rawtypes")
    private Class findSuitClass(List<Class> classes) {
        double ratio = 0.0;
        Class<?> result = classes.get(0);
        String beanName = beanClass.getSimpleName();
        for (Class clazz : classes) {
            double currentRatio = matchingRate(beanName, clazz.getSimpleName());
            if (ratio < currentRatio) {
                result = clazz;
                ratio = currentRatio;
            }
        }
        return result;
    }

    /**
     * 获取sessionBean中所有的remote class
     */
    @SuppressWarnings("rawtypes")
    public List<Class> getRemoteClasses() {
        List<Class> result = new ArrayList<Class>();
        // 如果本身是remote接口也算
        if (isRemoteClass() || beanClass.isInterface()) {
            result.add(beanClass);
        }
        // 注解上配置的remote接口
        Remote ann = beanClass.getAnnotation(Remote.class);
        if (ann != null) {
            for (Class clazz : ann.value()) {
                if (!result.contains(clazz)) {
                    result.add(clazz);
                }
            }
        }
        // class 的所有接口
        for (Class clazz : ClassUtils.getAllInterfaces(beanClass)) {
            // 当前类的所有接口, 如果接口上标注了remote注解则表示是一个remote class
            if (isRemoteClass(clazz) && !result.contains(clazz)) {
                result.add(clazz);
            }
        }
        //排序
        sortClasses(result);
        return result;
    }

    /**
     * 获取sessionBean中所有的local class
     */
    @SuppressWarnings("rawtypes")
    public List<Class> getLocalClasses() {
        List<Class> result = new ArrayList<Class>();
        if (isLocalClass() || (!isRemoteClass() && beanClass.isInterface())) {
            result.add(beanClass);
        }
        //注解上的local
        Local ann = beanClass.getAnnotation(Local.class);
        if (ann != null) {
            for (Class clazz : ann.value()) {
                if (!result.contains(clazz)) {
                    result.add(clazz);
                }
            }
        }
        // 所有接口
        for (Class clazz : ClassUtils.getAllInterfaces(beanClass)) {
            if (isLocalClass(clazz) && !result.contains(clazz)) {
                result.add(clazz);
            }
        }
        sortClasses(result);
        return result;
    }

    @SuppressWarnings({ "rawtypes" })
    private void sortClasses(List<Class> classes) {
        //排序
        if (classes.size() > 1) {
            Collections.sort(classes, new Comparator<Class>() {
                @Override
                public int compare(Class o1, Class o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            });
        }
    }

    /**
     * session Bean 是否存在{@linkplain Remote}注解的类,或其实现的接口中
     */
    public boolean hasRemoteClass() {
        return !getRemoteClasses().isEmpty();
    }

    /**
     * session Bean 是否存在{@linkplain Local}注解的类,或其实现的接口中
     */
    public boolean hasLocalClass() {
        return !getLocalClasses().isEmpty();
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

    protected static void validBeanClass(Class<?> clazz) {
        if (!(clazz.isInterface() || isRemoteClass(clazz) || isLocalClass(clazz) || isSessionBean(clazz))) {
            throw new IllegalArgumentException("class " + clazz.getName() + " not a javaee bean or interface");
        }
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
                return (String) ReflectionUtils.invokeMethod("mappedName", ann);
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
