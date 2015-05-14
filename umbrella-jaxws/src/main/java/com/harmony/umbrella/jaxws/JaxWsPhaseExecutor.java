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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
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

    private List<JaxWsContextHandler> handlers = new LinkedList<JaxWsContextHandler>();

    /**
     * 标记执行出现错误是抛出还是隐藏
     */
    private boolean hideTrowable = false;

    /**
     * 安静的执行{@linkplain #execute(JaxWsContext)}，不触发内部的handler
     * 
     * @param context
     * @param resultType
     * @return
     * @see #execute(JaxWsContext, Class)
     */
    public abstract <T> T executeQuite(JaxWsContext context, Class<T> resultType);

    /**
     * 安静的执行{@linkplain #execute(JaxWsContext)}，不触发内部的handler
     * 
     * @param context
     * @return
     * @see #execute(JaxWsContext, Class)
     */
    public Object executeQuite(JaxWsContext context) {
        return executeQuite(context, Object.class);
    }

    /**
     * 安静的异步执行{@linkplain #executeAsync(JaxWsContext)}
     * 
     * @param context
     * @param resultType
     * @return
     * @see #executeAsync(JaxWsContext)
     */
    public <T> Future<T> executeAsyncQuite(final JaxWsContext context, final Class<T> resultType) {
        FutureTask<T> task = new FutureTask<T>(new Callable<T>() {
            @Override
            public T call() throws Exception {
                return executeQuite(context, resultType);
            }
        });
        new Thread(task).start();
        return task;
    }

    /**
     * 安静的异步执行{@linkplain #executeAsync(JaxWsContext)}
     * 
     * @param context
     * @return
     */
    public Future<?> executeAsyncQuite(JaxWsContext context) {
        return executeAsyncQuite(context, Object.class);
    }

    @Override
    public <T> T execute(JaxWsContext context, Class<T> resultType) {
        Exception exception = null;
        T result = null;
        try {
            doBefore(context);
            LOG.debug("执行交互{}", context);
            long start = System.currentTimeMillis();
            result = executeQuite(context, resultType);
            LOG.debug("交互成功{}, 返回结果[{}], 交互耗时:{}ms", context, result, System.currentTimeMillis() - start);
            doCompletion(context, result);
        } catch (Exception e) {
            LOG.warn("交互失败{}", context, e);
            try {
                doThrowing(context, exception = e);
            } catch (Exception e1) {
                // ignore
            }
            throwOrHide(e);
        } finally {
            try {
                doFinally(context, result, exception);
            } catch (Exception e) {
                // ignore
            }
            if (context.contains(JaxWsGraph.JAXWS_CONTEXT_GRAPH)) {
                Object graph = context.get(JaxWsGraph.JAXWS_CONTEXT_GRAPH);
                LOG.info("执行情况概要如下:{}", graph);
                try {
                    persistGraph((JaxWsGraph) graph);
                } catch (Exception e) {
                    LOG.debug("保存失败, JaxWs执行概要无法正常保存", e);
                }
            }
        }
        return result;
    }

    /**
     * {@linkplain JaxWsPhaseExecutor}中没有在{@linkplain JaxWsContext}中设置
     * {@linkplain JaxWsGraph}.
     * <p>
     * 所以子类必须在{@linkplain JaxWsContext}设置 {@linkplain JaxWsGraph}.才会进入保存流程.
     * 
     * @param graph
     */
    protected abstract void persistGraph(JaxWsGraph graph);

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

    @Override
    public List<JaxWsContextHandler> getHandlers() {
        return Collections.unmodifiableList(this.handlers);
    }

    @Override
    public boolean addHandler(JaxWsContextHandler handler) {
        return this.handlers.add(handler);
    }

    @Override
    public boolean removeHandler(JaxWsContextHandler handler) {
        return this.handlers.remove(handler);
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

    private boolean doBefore(JaxWsContext context) {
        try {
            return doPrepare(context);
        } catch (JaxWsAbortException e) {
            LOG.warn("jaxws executor throw {}, abort execut", e);
            doAbort(context, e);
            return false;
        }
    }

    protected boolean doPrepare(JaxWsContext context) throws JaxWsAbortException {
        for (JaxWsContextHandler handler : handlers) {
            LOG.debug("{} do prepare", handler);
            if (!handler.preExecute(context))
                LOG.debug("{} PERPARE return false", handler);
            return false;
        }
        return true;
    }

    protected void doCompletion(JaxWsContext context, Object result) {
        for (JaxWsContextHandler handler : handlers) {
            LOG.debug("{} do completion", handler);
            handler.postExecute(context, result);
        }
    }

    protected void doThrowing(JaxWsContext context, Exception e) {
        for (JaxWsContextHandler handler : handlers) {
            LOG.debug("{} do throwing", handler);
            handler.throwing(context, e);
        }
    }

    protected void doAbort(JaxWsContext context, JaxWsAbortException e) {
        for (JaxWsContextHandler handler : handlers) {
            LOG.debug("{} do abort", handler);
            handler.abortExecute(context, e);
        }
    }

    protected void doFinally(JaxWsContext context, Object result, Exception e) {
        for (JaxWsContextHandler handler : handlers) {
            LOG.debug("{} do finally", handler);
            handler.finallyExecute(context, result, e);
        }
    }

    public boolean isHideTrowable() {
        return hideTrowable;
    }

    public void setHideTrowable(boolean hideTrowable) {
        this.hideTrowable = hideTrowable;
    }

}