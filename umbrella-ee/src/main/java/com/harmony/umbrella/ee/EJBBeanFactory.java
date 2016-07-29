package com.harmony.umbrella.ee;

import java.lang.annotation.Annotation;

import com.harmony.umbrella.beans.BeanFactory;
import com.harmony.umbrella.beans.BeansException;

/**
 * @author wuxii@foxmail.com
 */
public interface EJBBeanFactory extends BeanFactory {

    void autowrie(Object bean) throws BeansException;

    <T> T lookup(String jndi) throws BeansException;

    <T> T lookup(Class<T> clazz) throws BeansException;

    <T> T lookup(Class<T> clazz, Annotation... ann) throws BeansException;

}
