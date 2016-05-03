package com.harmony.umbrella.ws;

import java.util.Map;

/**
 * 基于{@linkplain com.harmony.umbrella.ws.jaxws.JaxWsExecutor}的结果回调支持
 *
 * @param <V> 数据交互结果
 * @author wuxii@foxmail.com
 */
public interface ResponseCallback<V> {

    /**
     * 回调处理方法
     *
     * @param result  接口返回的结果
     * @param content 执行的上下文内容{@linkplain Context#getContextMap()}
     */
    void handle(V result, Map<String, Object> content);

}
