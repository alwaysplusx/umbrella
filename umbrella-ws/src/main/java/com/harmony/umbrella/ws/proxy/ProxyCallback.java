package com.harmony.umbrella.ws.proxy;

import java.util.Map;

/**
 * one sync method one sync callback
 * 
 * @param <T>
 *            同步的实体类
 * @param <R>
 *            同步返回的结果
 * @author wuxii@foxmail.com
 */
public interface ProxyCallback<T, R> {

    /**
     * 回调方法之, 执行同步前
     * 
     * @param obj
     *            同步的对象
     */
    void forward(T obj, Map<String, Object> content);

    /**
     * 回调方法之, 同步成功后
     * 
     * @param obj
     *            同步的对象
     * @param result
     *            同步结果
     */
    void success(T obj, R result, Map<String, Object> content);

    /**
     * 回调方法, 同步失败
     * 
     * @param obj
     *            同步对象
     * @param throwable
     *            同步失败原因
     */
    void failed(T obj, Throwable throwable, Map<String, Object> content);

}
