/*
 * Copyright 2002-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.harmony.umbrella.ws;

import java.util.concurrent.Future;

/**
 * @author wuxii@foxmail.com
 */
public interface ProxyExecutor {

    public static final String WS_EXECUTION_GRAPH = ProxyExecutor.class.getName() + ".WS_EXECUTION_GRAPH";

    /**
     * 执行交互
     * <p>
     * <em>tips:如果接口中包含不定参数一定要特别注意，将不定参数当作数组一并输入</em>
     * 
     * @param context
     *            执行上下文
     * @return 执行结果,如果web service返回参数为void则返回null
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
     * @return 交互结果
     */
    <T> T execute(Context context, Class<T> resultType);

    /**
     * 执行交互, 提供各时段的visitor
     * 
     * @param context
     *            执行上下文
     * @param visitors
     *            执行的访问者
     * @return
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
     * @return
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
     */
    <V> void executeAsync(Context context, AsyncCallback<V> callback);

}
