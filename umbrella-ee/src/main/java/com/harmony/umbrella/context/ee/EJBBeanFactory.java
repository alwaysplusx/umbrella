package com.harmony.umbrella.context.ee;

import com.harmony.umbrella.core.BeanFactory;
import com.harmony.umbrella.core.BeansException;

import javax.ejb.EJB;
import java.util.Properties;

/**
 * @author wuxii@foxmail.com
 */
public interface EJBBeanFactory extends BeanFactory {

    <T> Object lookup(String jndi) throws BeansException;

    <T> T lookup(Class<T> clazz) throws BeansException;

    <T> T lookup(Class<T> clazz, EJB ejbAnnotation) throws BeansException;

    <T> T lookup(BeanDefinition beanDefinition) throws BeansException;

    <T> T lookup(BeanDefinition beanDefinition, EJB ejbAnnotation) throws BeansException;

    void setContextProperties(Properties properties);

}
