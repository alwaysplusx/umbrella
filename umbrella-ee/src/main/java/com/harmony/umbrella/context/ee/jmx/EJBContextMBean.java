package com.harmony.umbrella.context.ee.jmx;

/**
 * EJB Application Context JMX管理扩展
 * 
 * @author wuxii@foxmail.com
 */
public interface EJBContextMBean {

    /**
     * 清除已经加载的属性
     */
    void resetProperties();

    /**
     * 查看是否存在类型为clazz的会话bean
     */
    boolean exists(String className);

    /**
     * 当前上下文属性文件所在位置
     */
    String propertiesFileLocation();

    /**
     * 展示现在所有的资源属性
     */
    String showProperties();

}
