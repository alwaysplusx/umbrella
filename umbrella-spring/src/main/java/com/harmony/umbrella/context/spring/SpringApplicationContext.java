package com.harmony.umbrella.context.spring;

import java.net.URL;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import com.harmony.umbrella.context.ApplicationContext;

/**
 * @author wuxii@foxmail.com
 */
public class SpringApplicationContext extends ApplicationContext implements BeanFactory {

    private org.springframework.context.ApplicationContext springContext;

    public SpringApplicationContext(org.springframework.context.ApplicationContext springContext, URL url) {
        this.springContext = springContext;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getBean(String beanName, String scope) throws BeansException {
        return (T) springContext.getBean(beanName);
    }

    @Override
    public <T> T getBean(Class<T> beanClass) throws BeansException {
        return springContext.getBean(beanClass);
    }

    @Override
    public <T> T getBean(Class<T> beanClass, String scope) throws BeansException {
        return springContext.getBean(beanClass);
    }

    @Override
    public void init() {
    }

    @Override
    public void destroy() {
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object getBean(String name) throws BeansException {
        return springContext.getBean(name);
    }

    @Override
    public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        return springContext.getBean(name, requiredType);
    }

    @Override
    public Object getBean(String name, Object... args) throws BeansException {
        return springContext.getBean(name, args);
    }

    @Override
    public <T> T getBean(Class<T> requiredType, Object... args) throws BeansException {
        return springContext.getBean(requiredType, args);
    }

    @Override
    public boolean containsBean(String name) {
        return springContext.containsBean(name);
    }

    @Override
    public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        return springContext.isSingleton(name);
    }

    @Override
    public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
        return springContext.isPrototype(name);
    }

    @Override
    public boolean isTypeMatch(String name, Class<?> targetType) throws NoSuchBeanDefinitionException {
        return springContext.isTypeMatch(name, targetType);
    }

    @Override
    public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        return springContext.getType(name);
    }

    @Override
    public String[] getAliases(String name) {
        return springContext.getAliases(name);
    }

}
