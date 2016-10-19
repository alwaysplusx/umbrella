package com.harmony.umbrella.core;

/**
 * bean加载
 *
 * @author wuxii@foxmail.com
 */
public interface BeanFactory {

    /**
     * 单例
     */
    String SINGLETON = "singleton";

    /**
     * 原型
     */
    String PROTOTYPE = "prototype";

    /**
     * 根据bean的名称加载指定bean，默认获取单例的bean
     *
     * @param beanName
     *            需要获取的bean名称
     * @return 指定名称的bean
     */
    <T> T getBean(String beanName) throws BeansException;

    /**
     * 加载一个指定类型的bean
     *
     * @param beanName
     *            需要获取的bean名称
     * @param scope
     *            bean scope
     * @return 指定名称的bean
     */
    <T> T getBean(String beanName, String scope) throws BeansException;

    /**
     * 默认加载一个单例的bean
     *
     * @param beanClass
     *            需要获取的bean类
     * @return 指定类型的bean
     */
    <T> T getBean(Class<T> beanClass) throws BeansException;

    /**
     * 加载一个指定类型的bean
     *
     * @param beanClass
     *            需要获取的bean类
     * @param scope
     *            bean scope
     * @return 指定类型的bean
     */
    <T> T getBean(Class<T> beanClass, String scope) throws BeansException;

}
