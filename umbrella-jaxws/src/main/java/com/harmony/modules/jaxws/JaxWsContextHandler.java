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
package com.harmony.modules.jaxws;

import java.io.Serializable;

/**
 * 执行内容处理器
 * 
 * @author wuxii@foxmail.com
 *
 */
public interface JaxWsContextHandler extends Serializable {

    /**
     * 执行前被调用
     * 
     * @param context
     *            执行上下文
     * @throws JaxWsAbortException
     */
    boolean preExecute(JaxWsContext context) throws JaxWsAbortException;

    /**
     * 异常取消执行
     * 
     * @param context
     * @param exception
     */
    void abortExecute(JaxWsContext context, JaxWsAbortException exception);

    /**
     * 正确执行后调用
     * 
     * @param context
     *            执行上下文
     * @param result
     *            执行的结果
     */
    void postExecute(JaxWsContext context, Object result);

    /**
     * 执行异常后被调用
     * 
     * @param context
     *            执行上下文
     * @param throwable
     *            抛出的异常
     */
    void throwing(JaxWsContext context, Throwable throwable);

    /**
     * 在finally块中被调用
     * 
     * @param context
     * @param result
     * @param e
     */
    void finallyExecute(JaxWsContext context, Object result, Exception e);
}
