package com.harmony.umbrella.ee;

/**
 * EJB中的会话Bean信息
 * <p>
 * 包含以下信息:
 * <ul>
 * <li>jndi
 * <li>bean definition
 * <li>一个被缓存着的实例(可以做单例使用)
 * </ul>
 * 
 * @author wuxii@foxmail.com
 */
public interface SessionBean {

    /**
     * 缓存着的bean
     * 
     * @return ejb bean
     */
    Object getBean();

    /**
     * bean对应的jndi
     * 
     * @return jndi
     */
    String getJndi();

    /**
     * 标记是否支持缓存
     */
    boolean isCacheable();

    /**
     * 标识是否为包裹着的实例
     * <p>
     * 由于各个服务器实现是不同的, 在创建EJB容器时候会将用户定义的Bean通过动态代理的方式实现
     * 
     */
    boolean isWrapped();

    /**
     * sessionBean对于的beanDefinition定义
     * 
     * @return bean定义信息
     */
    BeanDefinition getBeanDefinition();

}