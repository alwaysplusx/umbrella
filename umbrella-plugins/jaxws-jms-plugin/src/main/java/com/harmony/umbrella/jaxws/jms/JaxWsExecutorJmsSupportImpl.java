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
package com.harmony.umbrella.jaxws.jms;

import java.util.concurrent.Future;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import com.harmony.umbrella.jaxws.JaxWsAsyncCallback;
import com.harmony.umbrella.jaxws.JaxWsContext;
import com.harmony.umbrella.jaxws.JaxWsExecutor;
import com.harmony.umbrella.jaxws.JaxWsPhaseVisitor;
import com.harmony.umbrella.jaxws.impl.JaxWsCXFExecutor;
import com.harmony.umbrella.jaxws.support.JaxWsContextSender;
import com.harmony.umbrella.jaxws.support.JaxWsExecutorSupport;

/**
 * {@linkplain JaxWsExecutor}的JMS扩展, 便于使用JMS模块.
 * <p>
 * 扩展的方法 {@linkplain #send(JaxWsContext)}.
 * 将所要执行的webservice上下文发送给jms.等待jms的消费者接收消息后执行真正的交互
 * <p>
 * 主要功能为:将执行web service在结构上解耦
 * 
 * @author wuxii@foxmail.com
 */
@Stateless(mappedName = "JaxWsExecutorJmsSupportImpl")
@Remote(JaxWsExecutorSupport.class)
public class JaxWsExecutorJmsSupportImpl implements JaxWsExecutorSupport {

    @EJB
    private JaxWsContextSender sender;
    private JaxWsExecutor executor;

    public JaxWsExecutorJmsSupportImpl() {
        this(new JaxWsCXFExecutor(), null);
    }

    public JaxWsExecutorJmsSupportImpl(JaxWsExecutor executor, JaxWsContextSender sender) {
        this.executor = executor;
        this.sender = sender;
    }

    @Override
    public <T> T execute(JaxWsContext context, Class<T> resultType) {
        return executor.execute(context, resultType);
    }

    @Override
    public Object execute(JaxWsContext context) {
        return executor.execute(context);
    }

    @Override
    public <T> Future<T> executeAsync(JaxWsContext context, Class<T> resultType) {
        return executor.executeAsync(context, resultType);
    }

    @Override
    public Future<?> executeAsync(JaxWsContext context) {
        return executor.executeAsync(context);
    }

    @Override
    public <V> void executeAsync(JaxWsContext context, JaxWsAsyncCallback<V> callback) {
        this.executor.executeAsync(context, callback);
    }

    @Override
    public boolean send(JaxWsContext context) {
        if (sender == null)
            throw new NullPointerException();
        return sender.send(context);
    }

    public void setJaxWsExecutor(JaxWsExecutor executor) {
        this.executor = executor;
    }

    public void setJaxWsContextSender(JaxWsContextSender sender) {
        this.sender = sender;
    }

    @Override
    public <T> T execute(JaxWsContext context, Class<T> resultType, JaxWsPhaseVisitor... visitors) {
        return executor.execute(context, resultType, visitors);
    }

    @Override
    public Object execute(JaxWsContext context, JaxWsPhaseVisitor... visitors) {
        return executor.execute(context, visitors);
    }

}
