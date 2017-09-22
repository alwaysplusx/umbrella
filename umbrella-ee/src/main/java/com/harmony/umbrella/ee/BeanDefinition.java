package com.harmony.umbrella.ee;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Remote;

import org.springframework.util.ClassUtils;


/**
 * @author wuxii@foxmail.com
 */
public class BeanDefinition {

    /**
     * beanClass 会话bean的类
     */
    public final Class<?> beanClass;

    private Class<?>[] remoteClasses;

    private Class<?>[] localClasses;

    public BeanDefinition(Class<?> beanClass) {
        this.beanClass = beanClass;
        this.remoteClasses = findRemoteClasses(beanClass);
        this.localClasses = findLocalClasses(beanClass);
    }

    /**
     * bean 类型
     *
     * @return 被描述的类
     */
    public Class<?> getBeanClass() {
        return beanClass;
    }

    public Class<?>[] getRemoteClasses() {
        return remoteClasses;
    }

    public Class<?>[] getLocalClasses() {
        return localClasses;
    }

    public boolean hasAnnotationOf(Class<? extends Annotation> annCls) {
        return getAnnotation(annCls) != null;
    }

    public <T extends Annotation> T getAnnotation(Class<T> annCls) {
        return beanClass.getAnnotation(annCls);
    }

    private Class[] findRemoteClasses(Class<?> clazz) {
        List<Class> remoteClasses = new ArrayList<Class>();
        if (isRemoteClass(clazz) || (clazz.isInterface() && !isLocalClass(clazz))) {
            remoteClasses.add(clazz);
        } else {
            Remote ann = clazz.getAnnotation(Remote.class);
            if (ann != null) {
                Class[] classes = ann.value();
                for (Class c : classes) {
                    if (isRemoteClass(c)) {
                        remoteClasses.add(c);
                    }
                }
            }
            Class<?>[] interfaces = ClassUtils.getAllInterfaces(beanClass);
            for (Class<?> c : interfaces) {
                if (isRemoteClass(c)) {
                    remoteClasses.add(c);
                }
            }
        }
        return remoteClasses.toArray(new Class[remoteClasses.size()]);
    }

    private Class<?>[] findLocalClasses(Class<?> clazz) {
        List<Class> localClasses = new ArrayList<Class>();
        if (isLocalClass(clazz) || (clazz.isInterface() && !isRemoteClass(clazz))) {
            localClasses.add(clazz);
        } else {
            Local ann = clazz.getAnnotation(Local.class);
            if (ann != null) {
                Class[] classes = ann.value();
                for (Class c : classes) {
                    if (isLocalClass(c)) {
                        localClasses.add(c);
                    }
                }
            }
            Class<?>[] interfaces = ClassUtils.getAllInterfaces(beanClass);
            for (Class<?> c : interfaces) {
                if (isLocalClass(c)) {
                    localClasses.add(c);
                }
            }
        }
        return localClasses.toArray(new Class[localClasses.size()]);
    }

    // utils method
    private boolean isRemoteClass(Class<?> clazz) {
        return clazz.isInterface() && clazz.getAnnotation(Remote.class) != null;
    }

    private boolean isLocalClass(Class<?> clazz) {
        return clazz.isInterface() && clazz.getAnnotation(Local.class) != null;
    }

}
