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
package com.harmony.umbrella.ws.jaxws;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.util.Exceptions;
import com.harmony.umbrella.ws.AsyncCallback;
import com.harmony.umbrella.ws.Context;
import com.harmony.umbrella.ws.WebServiceGraph;
import com.harmony.umbrella.ws.PhaseVisitor;
import com.harmony.umbrella.ws.WebServiceAbortException;
import com.harmony.umbrella.ws.WebServiceException;

/**
 * 将{@linkplain JaxWsExecutor}的执行分为各个周期的抽象类
 * 
 * @author wuxii@foxmail.com
 */
public abstract class JaxWsPhaseExecutor implements JaxWsExecutor {

    protected static final Logger LOG = LoggerFactory.getLogger(JaxWsPhaseExecutor.class);

    /**
     * 标记执行出现错误是抛出还是隐藏
     */
    private boolean hideTrowable = false;

    public abstract <T> T executeQuite(Context context, Class<T> resultType);

    @Override
    public <T> T execute(Context context, Class<T> resultType) {
        return execute(context, resultType, new PhaseVisitor[0]);
    }

    @Override
    public <T> T execute(Context context, Class<T> resultType, PhaseVisitor... visitors) {
        T result = null;
        try {
            if (!doBefore(context, visitors)) {
                return null;
            }
            result = executeQuite(context, resultType);
            doCompletion(result, context, visitors);
        } catch (WebServiceAbortException e) {
            doAbort((WebServiceAbortException) e, context, visitors);
        } catch (Exception e) {
            doThrowing(e, context, visitors);
            throwOrHide(e);
        } finally {
            if (context.contains(WebServiceGraph.WS_CONTEXT_GRAPH)) {
                Object graph = context.get(WebServiceGraph.WS_CONTEXT_GRAPH);
                LOG.info("执行情况概要如下:{}", graph);
            }
        }
        return result;
    }

    @Override
    public Object execute(Context context, PhaseVisitor... visitors) {
        return execute(context, Object.class, visitors);
    }

    @Override
    public Object execute(Context context) {
        return execute(context, Object.class);
    }

    @Override
    public <T> Future<T> executeAsync(final Context context, final Class<T> resultType) {
        FutureTask<T> task = new FutureTask<T>(new Callable<T>() {
            @Override
            public T call() throws Exception {
                return execute(context, resultType);
            }
        });
        new Thread(task).start();
        return task;
    }

    @Override
    public Future<?> executeAsync(Context context) {
        return (Future<?>) executeAsync(context, Object.class);
    }

    /**
     * 将执行中抛出的异常在方法中通过判断是否抛出
     * 
     * @param e
     */
    protected void throwOrHide(Exception e) {
        if (!hideTrowable) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new WebServiceException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V> void executeAsync(final Context context, AsyncCallback<V> callback) throws WebServiceException {
        Future<V> future = (Future<V>) executeAsync(context);
        while (!future.isCancelled()) {
            if (future.isDone()) {
                try {
                    V result = future.get();
                    if (callback != null)
                        callback.handle(result, context.getContextMap());
                    break;
                } catch (Exception e) {
                    throw Exceptions.unchecked(e);
                }
            }
        }
    }

    protected boolean doBefore(Context context, PhaseVisitor[] visitors) throws WebServiceAbortException {
        if (visitors != null && visitors.length > 0) {
            for (PhaseVisitor visitor : visitors) {
                if (!visitor.visitBefore(context)) {
                    return false;
                }
            }
        }
        return true;
    }

    protected void doAbort(WebServiceAbortException ex, Context context, PhaseVisitor[] visitors) {
        if (visitors != null && visitors.length > 0) {
            for (PhaseVisitor visitor : visitors) {
                visitor.visitAbort(ex, context);
            }
        }
    }

    protected void doCompletion(Object result, Context context, PhaseVisitor[] visitors) {
        if (visitors != null && visitors.length > 0) {
            for (PhaseVisitor visitor : visitors) {
                visitor.visitCompletion(result, context);
            }
        }
    }

    protected void doThrowing(Exception throwable, Context context, PhaseVisitor[] visitors) {
        if (visitors != null && visitors.length > 0) {
            for (PhaseVisitor visitor : visitors) {
                visitor.visitThrowing(throwable, context);
            }
        }
    }

}