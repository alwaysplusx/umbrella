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
package com.harmony.umbrella.ws;

/**
 * 执行的各个周期的访问者.
 * 
 * @author wuxii@foxmail.com
 */
public interface ContextVisitor {

    /**
     * 在执行前被调用
     * 
     * @param context
     *            执行的上下文
     * @return {@code true}告知调用者可以继续其业务逻辑, {@code false}不可以继续其业务逻辑
     * @throws WebServiceAbortException
     *             必须马上取消调用
     */
    boolean visitBefore(Context context) throws WebServiceAbortException;

    /**
     * 执行被取消时候调用
     * 
     * @param ex
     *            取消的异常
     * @param context
     *            执行的上下文
     */
    void visitAbort(WebServiceAbortException ex, Context context);

    /**
     * 执行成功时候调用
     * 
     * @param result
     *            执行结果
     * @param context
     *            执行上下文
     */
    void visitCompletion(Object result, Context context);

    /**
     * 执行异常时候被调用
     * 
     * @param throwable
     *            异常信息
     * @param context
     *            执行上下文
     */
    void visitThrowing(Throwable throwable, Context context);

    /**
     * 在调用的finally块中被调用
     * 
     * @param result
     *            执行结果
     * @param throwable
     *            执行异常内容， 如果执行没有异常则为null
     * @param context
     *            执行上下文
     */
    void visitFinally(Object result, Throwable throwable, Context context);

}
