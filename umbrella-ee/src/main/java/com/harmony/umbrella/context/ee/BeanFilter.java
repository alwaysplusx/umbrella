package com.harmony.umbrella.context.ee;

/**
 * 对环境中查找到的bean进行过滤
 * 
 * @author wuxii@foxmail.com
 */
public interface BeanFilter {

    /**
     * 判断jndi对应的bean是否为所需要的bean
     * 
     * @param jndi
     *            jndi名称
     * @param bean
     *            bean实例
     */
    boolean accept(String jndi, Object bean);

}