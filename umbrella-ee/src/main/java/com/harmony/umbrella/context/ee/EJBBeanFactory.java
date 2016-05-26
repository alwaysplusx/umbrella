package com.harmony.umbrella.context.ee;

import javax.ejb.EJB;

import com.harmony.umbrella.core.BeanFactory;
import com.harmony.umbrella.core.BeansException;

/**
 * @author wuxii@foxmail.com
 */
public interface EJBBeanFactory extends BeanFactory {

    <T> Object lookup(String jndi) throws BeansException;

    <T> T lookup(Class<T> clazz) throws BeansException;

    <T> T lookup(Class<T> clazz, EJB ejbAnnotation) throws BeansException;

    <T> T lookup(BeanDefinition beanDefinition) throws BeansException;

    <T> T lookup(BeanDefinition beanDefinition, EJB ejbAnnotation) throws BeansException;

}
