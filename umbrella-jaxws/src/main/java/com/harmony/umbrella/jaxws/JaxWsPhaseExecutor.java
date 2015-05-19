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

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.util.Exceptions;

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

    public abstract <T> T executeQuite(JaxWsContext context, Class<T> resultType);

    @Override
    public <T> T execute(JaxWsContext context, Class<T> resultType) {
        return execute(context, resultType, (JaxWsPhaseVisitor) null);
    }

    @Override
    public <T> T execute(JaxWsContext context, Class<T> resultType, JaxWsPhaseVisitor... visitors) {
        Exception ex = null;
        T result = null;
        try {
            LOG.debug("执行交互{}", context);
            if (!doBefore(context)) {
                return null;
            }
            long start = System.currentTimeMillis();
            result = executeQuite(context, resultType);
            LOG.debug("交互成功{}, 返回结果[{}], 交互耗时:{}ms", context, result, System.currentTimeMillis() - start);
            doCompletion(result, context, visitors);
        } catch (JaxWsAbortException e) {
            LOG.info("交互取消{}", context, e);
            doAbort((JaxWsAbortException) (ex = e), context, visitors);
        } catch (Exception e) {
            LOG.warn("交互失败{}", context, e);
            doThrowing(ex = e, context, visitors);
            throwOrHide(e);
        } finally {
            doFinally(result, ex, context, visitors);
            if (context.contains(JaxWsGraph.JAXWS_CONTEXT_GRAPH)) {
                Object graph = context.get(JaxWsGraph.JAXWS_CONTEXT_GRAPH);
                LOG.info("执行情况概要如下:{}", graph);
            }
        }
        return result;
    }

    @Override
    public Object execute(JaxWsContext context, JaxWsPhaseVisitor... visitors) {
        return execute(context, Object.class, visitors);
    }

    @Override
    public Object execute(JaxWsContext context) {
        return execute(context, Object.class);
    }

    @Override
    public <T> Future<T> executeAsync(final JaxWsContext context, final Class<T> resultType) {
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
    public Future<?> executeAsync(JaxWsContext context) {
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
            throw new JaxWsException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V> void executeAsync(final JaxWsContext context, JaxWsAsyncCallback<V> callback) throws JaxWsException {
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

    protected boolean doBefore(JaxWsContext context, JaxWsPhaseVisitor... visitors) throws JaxWsAbortException {
        if (visitors != null && visitors.length > 0) {
            for (JaxWsPhaseVisitor visitor : visitors) {
                if (!visitor.visitBefore(context)) {
                    return false;
                }
            }
        }
        return true;
    }

    protected void doAbort(JaxWsAbortException ex, JaxWsContext context, JaxWsPhaseVisitor... visitors) {
        if (visitors != null && visitors.length > 0) {
            for (JaxWsPhaseVisitor visitor : visitors) {
                visitor.visitAbort(ex, context);
            }
        }
    }

    protected void doCompletion(Object result, JaxWsContext context, JaxWsPhaseVisitor... visitors) {
        if (visitors != null && visitors.length > 0) {
            for (JaxWsPhaseVisitor visitor : visitors) {
                visitor.visitCompletion(result, context);
            }
        }
    }

    protected void doThrowing(Exception throwable, JaxWsContext context, JaxWsPhaseVisitor... visitors) {
        if (visitors != null && visitors.length > 0) {
            for (JaxWsPhaseVisitor visitor : visitors) {
                visitor.visitThrowing(throwable, context);
            }
        }
    }

    protected void doFinally(Object result, Exception throwable, JaxWsContext context, JaxWsPhaseVisitor... visitors) {
        if (visitors != null && visitors.length > 0) {
            for (JaxWsPhaseVisitor visitor : visitors) {
                visitor.visitFinally(result, throwable, (JaxWsGraph) context.get(JaxWsGraph.JAXWS_CONTEXT_GRAPH), context);
            }
        }
    }
    
}