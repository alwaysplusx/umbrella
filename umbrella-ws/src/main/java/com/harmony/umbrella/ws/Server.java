package com.harmony.umbrella.ws;

/**
 * 接口服务
 * 
 * @author wuxii@foxmail.com
 */
public interface Server {

    /**
     * 接口实例
     * 
     * @return 接口实际业务bean
     */
    Object getServiceBean();

    /**
     * 接口类
     * 
     * @return 接口class
     */
    Class<?> getServiceClass();

}
