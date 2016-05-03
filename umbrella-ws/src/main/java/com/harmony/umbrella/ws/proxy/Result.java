package com.harmony.umbrella.ws.proxy;

/**
 * 
 * @author wuxii@foxmail.com
 */
public interface Result {

    boolean isWrapped();

    String getType();

    boolean isOk();

    String getMessage();

    Object getResult();

    <T> T unwrap(Class<T> requiredType);
}