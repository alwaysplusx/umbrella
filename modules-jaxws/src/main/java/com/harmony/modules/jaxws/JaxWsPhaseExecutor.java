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

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class JaxWsPhaseExecutor implements JaxWsExecutor {

    protected static final Logger LOG = LoggerFactory.getLogger(JaxWsPhaseExecutor.class);
    private List<JaxWsContextHandler> handlers = new LinkedList<JaxWsContextHandler>();
    private boolean hideTrowable = false;

    public abstract <T> T executeQuite(JaxWsContext context, Class<T> resultType);

    public Object executeQuite(JaxWsContext context) {
        return executeQuite(context, Object.class);
    }

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

    public Future<?> executeAsyncQuite(JaxWsContext context) {
        return executeAsyncQuite(context, Object.class);
    }

    public <T> T execute(JaxWsContext context, Class<T> resultType) {
        Exception exception = null;
        T result = null;
        try {
            doBefore(context);
            LOG.info("执行交互{}", context);
            long start = System.currentTimeMillis();
            result = executeQuite(context, resultType);
            LOG.info("交互成功{}, 返回结果[{}], 交互耗时:{}ms", context, result, System.currentTimeMillis() - start);
            doCompletion(context, result);
        } catch (Exception e) {
            LOG.warn("交互失败{}", context);
            LOG.warn("交互失败原因" + context, e);
            try {
                doThrowing(context, exception = e);
            } catch (Exception e1) {
                // ignore
            }
            if (!hideTrowable) {
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new JaxWsException(e);
            }
        } finally {
            try {
                doFinally(context, result, exception);
            } catch (Exception e) {
                // ignore
            }
        }
        return result;
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

    protected Method getServiceMethod(JaxWsContext context) throws NoSuchMethodException, SecurityException {
        return context.getMethod();
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