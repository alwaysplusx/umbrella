package com.harmony.umbrella.context.ee.support;

/**
 * 各种不同的容器对session bean的封装不同， 在用handler方式将bean包装拆解
 * 
 * @author wuxii@foxmail.com
 */
public interface WrappedBeanHandler {

    /**
     * 判断对应的bean是否为被包裹的bean
     * 
     * @param bean
     *            待检测的bean
     * @return if return true is wrapped bean
     */
    boolean isWrappedBean(Object bean);

    /**
     * 拆解bean
     * 
     * @param bean
     *            待拆解的bean
     * @return 拆解后的bean
     */
    Object unwrap(Object bean);

}
