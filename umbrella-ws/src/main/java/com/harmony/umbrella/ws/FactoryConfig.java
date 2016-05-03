package com.harmony.umbrella.ws;

/**
 * 创建时候的回调方法，负责创建前配置
 * 
 * @param <T>
 *            {@link org.apache.cxf.jaxws.JaxWsProxyFactoryBean
 *            JaxWsProxyFactoryBean}
 * @author wuxii@foxmail.com
 * @see org.apache.cxf.jaxws.JaxWsProxyFactoryBean
 * @see org.apache.cxf.jaxrs.JAXRSServerFactoryBean
 */
public interface FactoryConfig<T> {

    /**
     * 配置服务工厂的属性
     * 
     * @param serverFactoryBean
     *            服务工厂
     */
    void config(T serverFactoryBean);

}
