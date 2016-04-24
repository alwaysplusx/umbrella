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

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import javax.xml.ws.WebServiceException;

import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;

/**
 * 将{@linkplain ProxyExecutor}的执行分为各个周期的抽象类
 * 
 * @author wuxii@foxmail.com
 */
public abstract class ProxyExecutorSupport implements ProxyExecutor {

    protected static final Log LOG = Logs.getLog(ProxyExecutorSupport.class);

    /**
     * 标记执行出现错误是抛出还是隐藏
     */
    protected boolean raiseError = false;

    /**
     * 不触发visitor的情况下执行交互
     * 
     * @param context
     *            执行的上下文
     * @param resultType
     *            返回类型
     * @return 交互结果
     */
    public abstract <T> T executeQuite(Context context, Class<T> resultType);

    @Override
    public Object execute(Context context) {
        return execute(context, Object.class, new ContextVisitor[0]);
    }

    @Override
    public <T> T execute(Context context, Class<T> resultType) {
        return execute(context, resultType, new ContextVisitor[0]);
    }

    @Override
    public Object execute(Context context, ContextVisitor... visitors) {
        return execute(context, Object.class, visitors);
    }

    @Override
    public <T> T execute(Context context, Class<T> resultType, ContextVisitor... visitors) {
        T result = null;
        Exception ex = null;
        try {
            if (!doBefore(context, visitors)) {
                return null;
            }
            result = executeQuite(context, resultType);
            doCompletion(result, context, visitors);
        } catch (WebServiceAbortException e) {
            ex = e;
            doAbort(e, context, visitors);
        } catch (Exception e) {
            doThrowing(ex = e, context, visitors);
            throwOutException(e);
        } finally {
            doFinally(result, ex, context, visitors);
        }
        return result;
    }

    @Override
    public Future<?> executeAsync(Context context) {
        return executeAsync(context, Object.class);
    }

    @Override
    public <T> Future<T> executeAsync(final Context context, final Class<T> resultType) {
        FutureTask<T> task = new FutureTask<T>(new Callable<T>() {

            @Override
            public T call() throws Exception {
                LOG.info("start other thread execute context {}", context);
                return execute(context, resultType);
            }
        });
        new Thread(task).start();
        return task;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V> void executeAsync(final Context context, ResponseCallback<V> callback) throws WebServiceException {
        Future<V> future = (Future<V>) executeAsync(context);
        if (callback != null) {
            while (!future.isCancelled()) {
                if (future.isDone()) {
                    try {
                        callback.handle(future.get(), context.getContextMap());
                        break;
                    } catch (Exception e) {
                        throw new WebServiceException(e.getMessage(), e.getCause());
                    }
                }
            }
        }
    }

    /**
     * 将执行中抛出的异常在方法中通过判断是否抛出
     * 
     * @param e
     *            异常信息
     */
    private void throwOutException(Exception e) {
        if (raiseError) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new WebServiceException(e);
        }
        LOG.debug("ignore exception {}", e.toString());
    }

    /**
     * 调用访问者的before方法
     * 
     * @throws WebServiceAbortException
     *             取消执行异常, 抛出此异常后取消后续调用
     */
    protected boolean doBefore(Context context, ContextVisitor[] visitors) throws WebServiceAbortException {
        if (visitors != null && visitors.length > 0) {
            for (ContextVisitor visitor : visitors) {
                if (!visitor.visitBefore(context)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 调用访问者的abort方法
     */
    protected void doAbort(WebServiceAbortException ex, Context context, ContextVisitor[] visitors) {
        if (visitors != null && visitors.length > 0) {
            for (ContextVisitor visitor : visitors) {
                visitor.visitAbort(ex, context);
            }
        }
    }

    /**
     * 调用访问者的completion方法
     */
    protected void doCompletion(Object result, Context context, ContextVisitor[] visitors) {
        if (visitors != null && visitors.length > 0) {
            for (ContextVisitor visitor : visitors) {
                visitor.visitCompletion(result, context);
            }
        }
    }

    /**
     * 调用访问者的throwing方法
     */
    protected void doThrowing(Exception throwable, Context context, ContextVisitor[] visitors) {
        if (visitors != null && visitors.length > 0) {
            for (ContextVisitor visitor : visitors) {
                visitor.visitThrowing(throwable, context);
            }
        }
    }

    /**
     * 调用访问者的finally方法
     */
    protected void doFinally(Object result, Exception throwable, Context context, ContextVisitor[] visitors) {
        if (visitors != null && visitors.length > 0) {
            for (ContextVisitor visitor : visitors) {
                visitor.visitFinally(result, throwable, context);
            }
        }
    }

}