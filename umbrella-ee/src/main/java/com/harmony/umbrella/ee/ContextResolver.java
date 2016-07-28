package com.harmony.umbrella.ee;

/**
 * 在bean解析的基础上添加在context中查找对应bean的功能
 * 
 * @author wuxii@foxmail.com
 */
public interface ContextResolver extends BeanNameResolver {

    Object tryLookup(String jndi);

    SessionBean search(BeanDefinition beanDefinition);

}
