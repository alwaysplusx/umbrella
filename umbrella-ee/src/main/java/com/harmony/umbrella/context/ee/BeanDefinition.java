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

import com.harmony.umbrella.util.Assert;
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
     * beanClass 会话bean的类, 如果class没有标注session bean/local的注解并且是接口, 默认认为是remote接口
     */
    public final Class<?> beanClass;

    public final Annotation ann;

    public BeanDefinition(Class<?> beanClass) {
        this(beanClass, getSessionBeanAnnotation(beanClass));
    }

    public BeanDefinition(Class<?> beanClass, Annotation ann) {
        this.beanClass = beanClass;
        this.ann = ann;
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
     * 工具方法, 获取会话bean的mappedName
     */
    public String getMappedName() {
        String mappedName = getAnnotationValue("mappedName");
        if (StringUtils.isBlank(mappedName) && isSessionClass()) {
            return beanClass.getSimpleName();
        }
        return null;
    }

    /**
     * @see Stateless#name()
     */
    public String getName() {
        return getAnnotationValue("name");
    }

    /**
     * @see Stateless#description()
     */
    public String getDescription() {
        return getAnnotationValue("description");
    }

    /**
     * 标记了{@linkplain Stateless}
     */
    public boolean isStateless() {
        return isThatOf(Stateless.class);
    }

    /**
     * 标记了{@linkplain Stateful}
     */
    public boolean isStateful() {
        return isThatOf(Stateful.class);
    }

    /**
     * 标记了{@linkplain Singleton}
     */
    public boolean isSingleton() {
        return isThatOf(Singleton.class);
    }

    public boolean isThatOf(Class<? extends Annotation> annClass) {
        return ann != null && ann.getClass() == annClass;
    }

    /**
     * 注有{@linkplain Stateless}, {@linkplain Stateful}, {@linkplain Singleton}
     * 三类注解中的一个则为sessionBean
     */
    public boolean isSessionClass() {
        return ann != null;
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
    public Class<?> getSuitableRemoteClass() {
        if (isRemoteClass() || isLocalClass()) {
            return beanClass;
        }
        return findSuitClass(getRemoteClasses());
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
     * 鑾峰彇sessionBean涓墍鏈夌殑remote class
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

    private String getAnnotationValue(String name) {
        return ann == null ? null : getAnnotationValue(ann, name);
    }

    // utils method
    public static final String getAnnotationValue(Annotation ann, String methodName) {
        Assert.notNull(ann, "annotation is null");
        Assert.notNull(methodName, "annotation value name is null");
        try {
            return (String) ReflectionUtils.invokeMethod(methodName, ann);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    /**
     * 工具方法, 判断是否标有{@linkplain Remote}
     */
    public static boolean isRemoteClass(Class<?> clazz) {
        return clazz.isInterface() && (clazz.getAnnotation(Remote.class) != null || !isLocalClass(clazz));
    }

    /**
     * 工具方法, 判断是否标有{@linkplain Local}
     */
    public static boolean isLocalClass(Class<?> clazz) {
        return clazz.isInterface() && clazz.getAnnotation(Local.class) != null;
    }

    /**
     * 判断是否为会话bean
     */
    public static boolean isSessionClass(Class<?> clazz) {
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
