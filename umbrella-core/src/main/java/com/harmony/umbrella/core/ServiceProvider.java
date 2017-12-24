package com.harmony.umbrella.core;

/**
 * @author wuxii@foxmail.com
 */
public interface ServiceProvider {

    <T> T getService(Class<T> serviceType);

}
