package com.harmony.umbrella.ee;

import java.lang.annotation.Annotation;

import com.harmony.umbrella.core.BeanFactory;
import com.harmony.umbrella.core.BeansException;

/**
 * @author wuxii@foxmail.com
 */
public interface EJBBeanFactory extends BeanFactory {

    <T> T lookup(String jndi) throws BeansException;

    <T> T lookup(Class<T> clazz) throws BeansException;

    <T> T lookup(Class<T> clazz, Annotation... ann) throws BeansException;

}
