package com.harmony.umbrella.core;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.harmony.umbrella.util.ClassUtils;
import com.harmony.umbrella.util.ReflectionUtils;

/**
 * 通过类的反射{@linkplain Class#newInstance()}来创建Bean
 * 
 * @author wuxii@foxmail.com
 */
public class SimpleBeanFactory implements BeanFactory, Serializable {

    private static final long serialVersionUID = 1L;

    private final Map<Class<?>, Object> beans = new ConcurrentHashMap<Class<?>, Object>();

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getBean(String beanName) {
        try {
            Class<?> clazz = ClassUtils.forName(beanName);
            return (T) getBean(clazz, SINGLETON);
        } catch (ClassNotFoundException e) {
            throw new NoSuchBeanFoundException(e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getBean(String beanName, String scope) {
        try {
            Class<?> clazz = ClassUtils.forName(beanName);
            return (T) getBean(clazz, scope);
        } catch (ClassNotFoundException e) {
            throw new NoSuchBeanFoundException(e.getMessage(), e);
        }
    }

    @Override
    public <T> T getBean(Class<T> beanClass) {
        return getBean(beanClass, SINGLETON);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getBean(Class<T> beanClass, String scope) {
        if (SINGLETON.equals(scope)) {
            if (!beans.containsKey(beanClass)) {
                beans.put(beanClass, createBean(beanClass, null));
            }
            return (T) beans.get(beanClass);
        } else if (PROTOTYPE.equals(scope)) {
            return (T) createBean(beanClass, null);
        }
        throw new IllegalArgumentException("unsupport scope " + scope);
    }

    /**
     * 反射创建bean
     * 
     * @param beanClass
     * @param properties
     * @return
     */
    protected Object createBean(Class<?> beanClass, Map<String, Object> properties) {
        try {
            return ReflectionUtils.instantiateClass(beanClass);
        } catch (Exception e) {
            throw new NoSuchBeanFoundException(e.getMessage(), e);
        }
    }

}