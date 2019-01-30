package com.harmony.umbrella.core;

import org.springframework.beans.BeansException;

/**
 * bean加载
 *
 * @author wuxii@foxmail.com
 */
public interface BeanFactory {

    /**
     * 为bean自动注入所需要的依赖
     *
     * @param existingBean bean
     * @throws BeansException
     */
    void autowire(Object existingBean) throws BeansException;

    /**
     * 根据bean的名称加载指定bean，默认获取单例的bean
     *
     * @param beanName 需要获取的bean名称
     * @return 指定名称的bean
     */
    <T> T getBean(String beanName) throws BeansException;

    /**
     * 根据bean的名称以及类型加载对应的bean
     *
     * @param beanName    bean名称
     * @param requireType bean类型
     * @return bean
     * @throws BeansException
     */
    <T> T getBean(String beanName, Class<T> requireType) throws BeansException;

    /**
     * 默认加载一个单例的bean
     *
     * @param beanClass 需要获取的bean类
     * @return 指定类型的bean
     */
    <T> T getBean(Class<T> beanClass) throws BeansException;

}
