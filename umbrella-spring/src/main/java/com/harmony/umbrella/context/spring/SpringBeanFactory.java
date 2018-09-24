package com.harmony.umbrella.context.spring;

import org.springframework.context.ApplicationContext;

import com.harmony.umbrella.core.BeanFactory;
import com.harmony.umbrella.core.BeansException;

/**
 * @author wuxii@foxmail.com
 */
public class SpringBeanFactory implements BeanFactory {

    private org.springframework.context.ApplicationContext springContext;

    public SpringBeanFactory(ApplicationContext springContext) {
        this.springContext = springContext;
    }

    @Override
    public void autowire(Object existingBean) throws BeansException {
        springContext.getAutowireCapableBeanFactory().autowireBean(existingBean);
    }

    @Override
    public <T> T getBean(String beanName) throws BeansException {
        return (T) springContext.getBean(beanName);
    }

    @Override
    public <T> T getBean(String beanName, Class<T> requireType) throws BeansException {
        return springContext.getBean(beanName, requireType);
    }

    @Override
    public <T> T getBean(Class<T> beanClass) throws BeansException {
        return springContext.getBean(beanClass);
    }

}
