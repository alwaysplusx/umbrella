/*
 * Copyright 2002-2015 the original author or authors.
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
package com.harmony.umbrella.message.jms;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import com.harmony.umbrella.message.AbstractMessageListener;
import com.harmony.umbrella.util.ReflectionUtils;

/**
 * @author wuxii@foxmail.com
 */
public abstract class AbstractJmsMessageListener extends AbstractMessageListener implements MessageListener {

    protected boolean raiseError;

    /**
     * 为JMS提供. 只处理消息类型为{@linkplain com.harmony.umbrella.message.Message}的消息.
     * 如果不为该类型的消息则忽略
     * 
     * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
     */
    @Override
    public void onMessage(javax.jms.Message message) {
        LOG.debug("on message, current message is {}", message);
        Exception ex = null;
        if (message instanceof ObjectMessage) {
            try {
                Serializable object = ((ObjectMessage) message).getObject();
                if (object instanceof com.harmony.umbrella.message.Message) {
                    onMessage((com.harmony.umbrella.message.Message) object);
                    // right way
                    return;
                }
                ex = new IllegalStateException("illegal message type " + object.getClass().getName());
            } catch (JMSException e) {
                ex = e;
            }
        } else {
            ex = new IllegalStateException("jms message is not object message");
        }
        if (ex != null) {
            if (raiseError) {
                // 异常重新抛出
                ReflectionUtils.rethrowRuntimeException(ex);
            } else {
                // 忽略异常
                LOG.error(ex);
            }
        }
    }
}
