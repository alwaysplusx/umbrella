package com.harmony.umbrella.context.ee;

import javax.naming.Context;

/**
 * 在bean解析的基础上添加在context中查找对应bean的功能
 * 
 * @author wuxii@foxmail.com
 */
public interface ContextResolver extends BeanResolver {

    /**
     * 在context中lookup jndi对象
     * 
     * @param jndi
     *            环境中的jndi
     * @param context
     *            context
     * @return 如果context中没有对应的jndi返回null
     */
    Object tryLookup(String jndi, Context context);

    /**
     * 在context中查找beanDefinition对于的SessionBean
     * 
     * @param beanDefinition
     *            bean定义
     * @param context
     *            context
     * @return 与beanDefinition相对应的SessionBean， 没有找到返回null
     */
    SessionBean search(BeanDefinition beanDefinition, Context context);

    /**
     * 清除查找到的结果缓存
     */
    void clear();

}
