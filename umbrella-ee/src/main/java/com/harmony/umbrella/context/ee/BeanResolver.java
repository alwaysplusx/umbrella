package com.harmony.umbrella.context.ee;

import java.lang.annotation.Annotation;

import javax.naming.Context;
import javax.naming.NamingException;

/**
 * 根据bean的定义，在可以根据环境或者配置情况解析环境中对应的bean
 * 
 * @author wuxii@foxmail.com
 */
public interface BeanResolver {

    /**
     * {@linkplain javax.naming.Context}创建
     * 
     * @return {@linkplain javax.naming.Context}
     */
    Context getContext() throws NamingException;

    /**
     * 根据类猜想对应的名称
     * 
     * @param clazz
     *            class
     * @return jndis
     */
    String[] guessNames(Class<?> clazz);

    /**
     * 根据bean定义猜想对应的jndi名称
     * 
     * @param bd
     *            bean定义
     * @return jndis
     */
    String[] guessNames(BeanDefinition bd);

    /**
     * 根据bean定义猜想对应的jndi名称
     * 
     * @param bd
     *            bean定义
     * @param ann
     *            扩展注解, 一般用于字段上注解扩展
     * @return jndis
     */
    String[] guessNames(BeanDefinition bd, Annotation ann);

    /**
     * 通过bean定义,猜想可能的bean
     * 
     * @param bd
     *            bean定义
     * @return 可能的bean,如果没有找到返回null
     */
    Object[] guessBeans(BeanDefinition bd);

    /**
     * 通过bean定义,猜想可能的bean
     * 
     * @param bd
     *            bean定义
     * @param ann
     *            查找的属性信息
     * @return 可能的bean,如果没有找到返回null
     */
    Object[] guessBeans(BeanDefinition bd, Annotation ann);

}
