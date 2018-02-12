package com.harmony.umbrella.core;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 通过类的反射{@linkplain Class#newInstance()}来创建Bean
 * 
 * @author wuxii@foxmail.com
 */
public class SimpleBeanFactory extends AbstractBeanFactory implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final SimpleBeanFactory INSTANCE = new SimpleBeanFactory();

    @Override
    public <T> T getBean(String beanName, Class<T> requireType) throws BeansException {
        return getBean(requireType);
    }

    @Override
    public <T> T getBean(Class<T> beanClass) throws BeansException {
        return (T) createBean(beanClass);
    }

    @Override
    protected Object getBean(Field field) {
        return getBean(field.getType());
    }

    @Override
    protected Object getBean(Method method) {
        return getBean(method.getParameterTypes()[0]);
    }

    protected Object createBean(Class<?> beanClass) {
        try {
            return beanClass.newInstance();
        } catch (Exception e) {
            throw new NoSuchBeanFoundException(e.getMessage(), e);
        }
    }
}