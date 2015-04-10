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

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 将{@linkplain JaxWsExecutor}的执行分为各个周期的抽象类
 * @author wuxii@foxmail.com
 */
public abstract class JaxWsPhaseExecutor implements JaxWsExecutor {

    protected static final Logger LOG = LoggerFactory.getLogger(JaxWsPhaseExecutor.class);
    private List<JaxWsContextHandler> handlers = new LinkedList<JaxWsContextHandler>();
    private boolean hideTrowable = false;

    /**
     * 安静的执行{@linkplain #execute(JaxWsContext)}，不触发内部的handler
     * @param context
     * @param resultType
     * @return
     * @see #execute(JaxWsContext, Class)
     */
    public abstract <T> T executeQuite(JaxWsContext context, Class<T> resultType);

    /**
     * 安静的执行{@linkplain #execute(JaxWsContext)}，不触发内部的handler
     * @param context
     * @return
     * @see #execute(JaxWsContext, Class)
     */
    public Object executeQuite(JaxWsContext context) {
        return executeQuite(context, Object.class);
    }

    /**
     * 安静的异步执行{@linkplain #executeAsync(JaxWsContext)}
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
                    LOG.info("保存失败, JaxWs执行情况无法正常保存", e);
                }
            }
        }
        return result;
    }

    /**
     * {@linkplain JaxWsPhaseExecutor}中没有在{@linkplain JaxWsContext}中设置{@linkplain JaxWsGraph}.
     * <p>所以子类必须在{@linkplain JaxWsContext}设置{@linkplain JaxWsGraph}.才会进入保存流程.
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
        return (Future<?>) execute(context, Object.class);
    }

    @Override
    public List<JaxWsContextHandler> getHandlers() {
        return this.handlers;
    }

    @Override
    public boolean addHandler(JaxWsContextHandler handler) {
        return this.handlers.add(handler);
    }

    @Override
    public boolean removeHandler(JaxWsContextHandler handler) {
        return this.handlers.remove(handler);
    }

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
            doAbort(context, e);
            return false;
        }
    }

    protected boolean doPrepare(JaxWsContext context) throws JaxWsAbortException {
        for (JaxWsContextHandler handler : handlers) {
            if (!handler.preExecute(context))
                return false;
        }
        return true;
    }

    protected void doCompletion(JaxWsContext context, Object result) {
        for (JaxWsContextHandler handler : handlers) {
            handler.postExecute(context, result);
        }
    }

    protected void doThrowing(JaxWsContext context, Exception e) {
        for (JaxWsContextHandler handler : handlers) {
            handler.throwing(context, e);
        }
    }

    protected void doAbort(JaxWsContext context, JaxWsAbortException e) {
        for (JaxWsContextHandler handler : handlers) {
            handler.abortExecute(context, e);
        }
    }

    protected void doFinally(JaxWsContext context, Object result, Exception e) {
        for (JaxWsContextHandler handler : handlers) {
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