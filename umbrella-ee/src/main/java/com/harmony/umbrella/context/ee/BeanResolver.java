package com.harmony.umbrella.context.ee;

import java.util.Map;

/**
 * 根据bean的定义，在可以根据环境或者配置情况解析环境中对应的bean
 * 
 * @author wuxii@foxmail.com
 */
public interface BeanResolver {

    /**
     * {@linkplain javax.naming.Context}创建工厂
     * 
     * @return {@linkplain javax.naming.Context}
     */
    ContextFactory getContextFactory();

    /**
     * 通过bean定义,猜想可能的bean
     * 
     * @param beanDefinition
     *            bean定义
     * @return 可能的bean,如果没有找到返回null
     */
    Object guessBean(BeanDefinition beanDefinition);

    /**
     * 通过bean定义,猜想可能的bean
     * 
     * @param beanDefinition
     *            bean定义
     * @param properties
     *            查找的属性信息
     * @return 可能的bean,如果没有找到返回null
     */
    @SuppressWarnings("rawtypes")
    Object guessBean(BeanDefinition beanDefinition, Map properties);

    /**
     * 根据beanDefinition猜想context中对于的jndi， 并提供beanFilter过滤对应的猜想结果，选取最优的bean
     * 
     * @param beanDefinition
     *            bean定义
     * @param filter
     *            bean过滤
     * @return 猜想的最优解，如果未能找到返回null
     */
    Object guessBean(BeanDefinition beanDefinition, BeanFilter filter);

    /**
     * 根据beanDefinition猜想context中对于的jndi， 并提供beanFilter过滤对应的猜想结果，选取最优的bean
     * 
     * @param beanDefinition
     *            bean定义
     * @param properties
     *            查找的属性配置
     * @param filter
     *            bean校验过滤
     * @return 可能的bean,如果没有找到返回null
     */
    @SuppressWarnings("rawtypes")
    Object guessBean(BeanDefinition beanDefinition, Map properties, BeanFilter filter);

    /**
     * 查看bean是否与声明的类型匹配
     * 
     * @param declare
     *            声明的bean定义
     * @param bean
     *            待检验的bean
     * @return 符合定义的bean返回true
     */
    boolean isDeclareBean(BeanDefinition declare, Object bean);

}
