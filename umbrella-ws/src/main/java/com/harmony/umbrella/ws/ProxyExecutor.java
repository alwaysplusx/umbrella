package com.harmony.umbrella.ws;

import java.util.concurrent.Future;

/**
 * 代理执行者， 通过代理接口创建的上下文来实现最终的交互
 * 
 * @author wuxii@foxmail.com、
 */
public interface ProxyExecutor {

    /**
     * 接口交互监控视图
     */
    public static final String WS_EXECUTION_GRAPH = ProxyExecutor.class.getName() + ".WS_EXECUTION_GRAPH";

    /**
     * 执行交互
     * <p>
     * <em>tips:如果接口中包含不定参数一定要特别注意，将不定参数当作数组一并输入</em>
     * 
     * @param context
     *            执行上下文
     * @return 执行结果。如果服务返回参数为void则返回null
     */
    Object execute(Context context);

    /**
     * 执行交互，并返回指定类型的返回值
     * <p>
     * <em>tips:如果接口中包含不定参数一定要特别注意，将不定参数当作数组一并输入</em>
     * 
     * @param context
     *            上下文
     * @param resultType
     *            返回值类型
     * @param <T>
     *            服务返回值类型
     * @return 执行结果。如果服务返回参数为void则返回null
     */
    <T> T execute(Context context, Class<T> resultType);

    /**
     * 执行交互, 提供各时段的visitor
     * 
     * @param context
     *            执行上下文
     * @param visitors
     *            执行的访问者
     * @return 执行结果。如果服务返回参数为void则返回null
     */
    Object execute(Context context, ContextVisitor... visitors);

    /**
     * 执行交互, 提供visitor的各时段的访问
     * 
     * @param context
     *            执行上下文
     * @param resultType
     *            返回类型
     * @param visitors
     *            执行的访问者
     * @param <T>
     *            服务返回值类型
     * @return 执行结果。如果服务返回参数为void则返回null
     */
    <T> T execute(Context context, Class<T> resultType, ContextVisitor... visitors);

    /**
     * 异步调用web service
     * 
     * @param context
     *            执行 上下文
     * @return 执行结果
     */
    Future<?> executeAsync(Context context);

    /**
     * 异步执行交互
     * 
     * @param context
     *            执行上下文
     * @param resultType
     *            返回结果类型
     * @param <T>
     *            服务返回值类型
     * @return 执行结果
     */
    <T> Future<T> executeAsync(Context context, Class<T> resultType);

    /**
     * 异步调用,并提供对于结果的回调
     * 
     * @param context
     *            执行上下文
     * @param callback
     *            结果回调
     * @param <V>
     *            服务返回值类型
     */
    <V> void executeAsync(Context context, ResponseCallback<V> callback);

}
