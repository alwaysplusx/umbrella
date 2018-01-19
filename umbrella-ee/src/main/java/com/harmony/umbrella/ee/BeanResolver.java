package com.harmony.umbrella.ee;

import java.lang.annotation.Annotation;
import java.util.Map;

import javax.naming.Context;
import javax.naming.NamingException;

/**
 * 根据bean的定义，在可以根据环境或者配置情况解析环境中对应的bean
 * 
 * @author wuxii@foxmail.com
 */
public interface BeanResolver {

    /**
     * 当前运行环境的jndi context
     * 
     * @return jndi context
     * @throws NamingException
     */
    Context getContext() throws NamingException;

    /**
     * 通过对bean的定义信息猜测可能的jndi名称
     * 
     * @param bd
     *            bean definition
     * @return jnids
     */
    String[] guessNames(BeanDefinition bd);

    String[] guessNames(BeanDefinition bd, Annotation... ann);

    String[] guessNames(BeanDefinition bd, Map<String, Object> properties);

    SessionBean[] guessBeans(BeanDefinition bd);

    SessionBean[] guessBeans(BeanDefinition bd, Annotation... ann);

    SessionBean[] guessBeans(BeanDefinition bd, Map<String, Object> properties);

}
