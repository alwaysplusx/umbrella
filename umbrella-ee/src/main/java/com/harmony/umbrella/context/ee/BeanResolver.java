package com.harmony.umbrella.context.ee;

import javax.ejb.EJB;
import javax.naming.Context;

/**
 * 根据bean的定义，在可以根据环境或者配置情况解析环境中对应的bean
 * 
 * @author wuxii@foxmail.com
 */
public interface BeanResolver {

    /**
     * 通过配置的信息获取可猜想出的jndi
     * 
     * @param beanDefinition
     *            bean定义
     * @return jndis 猜想结果
     */
    String[] guessNames(BeanDefinition beanDefinition);

    /**
     * 根据配置的上下文的信息猜想环境中对应的bean jndi名称, 猜想的结构为root中存在的jndi
     * 
     * @param beanDefinition
     *            bean定义
     * @param context
     *            {@linkplain Context}
     * @return 所有猜想并在{@linkplain Context}中存在的jndi名称
     */
    String[] guessNames(BeanDefinition beanDefinition, Context context);

    String[] guessNames(BeanDefinition beanDefinition, EJB ejbAnnotation);

    String[] guessNames(BeanDefinition beanDefinition, EJB ejbAnnotation, Context context);

    Object guessBean(BeanDefinition beanDefinition, Context context);

    Object guessBean(BeanDefinition beanDefinition, EJB ejbAnnotation, Context context);

    /**
     * 根据beanDefinition猜想context中对于的jndi， 并提供beanFilter过滤对应的猜想结果，选取最优的bean
     * 
     * @param beanDefinition
     *            bean定义
     * @param context
     *            {@linkplain Context}
     * @param filter
     *            bean过滤
     * @return 猜想的最优解，如果未能找到返回null
     */
    Object guessBean(BeanDefinition beanDefinition, Context context, BeanFilter filter);

    Object guessBean(BeanDefinition beanDefinition, EJB ejbAnnotation, Context context, BeanFilter filter);

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
