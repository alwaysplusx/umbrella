/*
 * Copyright 2002-2014 the original author or authors.
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
package com.harmony.umbrella.jaxws;

import java.util.List;
import java.util.concurrent.Future;

/**
 * 执行者,真正的调用发生在这个类中
 * 
 * @author wuxii@foxmail.com
 *
 */
public interface JaxWsExecutor {

    /**
     * 执行交互，并返回指定类型的返回值
     * <p>
     * <em>tips:如果接口中包含不定参数一定要特别注意，将不定参数当作数组一并输入</em>
     * 
     * @param context
     * @param resultType
     * @return
     */
    <T> T execute(JaxWsContext context, Class<T> resultType);

    /**
     * 执行交互
     * <p>
     * <em>tips:如果接口中包含不定参数一定要特别注意，将不定参数当作数组一并输入</em>
     * 
     * @param context
     *            执行上下文
     * @return 执行结果,如果web service返回参数为void则返回null
     */
    Object execute(JaxWsContext context);

    /**
     * 异步执行交互
     * 
     * @param context
     *            执行上下文
     * @param resultType
     *            返回结果类型
     * @return 执行结果
     */
    <T> Future<T> executeAsync(JaxWsContext context, Class<T> resultType);

    /**
     * 异步调用web service
     * 
     * @param context
     *            执行 上下文
     * @return 执行结果
     */
    Future<?> executeAsync(JaxWsContext context);

    /**
     * 异步调用,并提供对于结果的回调
     * 
     * @param context
     *            执行上下文
     * @param callback
     *            结果回调
     */
    <V> void executeAsync(JaxWsContext context, JaxWsAsyncCallback<V> callback);

    /**
     * 增加一个处理器
     * 
     * @param handler
     * @return
     */
    boolean addHandler(JaxWsContextHandler handler);

    /**
     * 移除一个处理器
     * 
     * @param handler
     * @return
     */
    boolean removeHandler(JaxWsContextHandler handler);

    /**
     * 当前Executor所拥有的处理器
     * 
     * @return {@linkplain java.util.Collections#unmodifiableList(List)}
     */
    List<JaxWsContextHandler> getHandlers();

}
