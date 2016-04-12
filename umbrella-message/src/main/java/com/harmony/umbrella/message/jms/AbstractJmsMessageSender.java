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
package com.harmony.umbrella.message.jms;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import com.harmony.umbrella.message.Message;
import com.harmony.umbrella.message.MessageException;

/**
 * 基于JMS的消息发送基础抽象类
 * 
 * @author wuxii@foxmail.com
 */
public abstract class AbstractJmsMessageSender implements JmsMessageSender {

    @Override
    public boolean send(Message message) throws MessageException {
        return send(message, createJmsConfig());
    }

    @Override
    public boolean send(Message message, JmsConfig config) throws MessageException {
        try {
            return sendJmsMessage(message, config);
        } catch (JMSException e) {
            throw new MessageException(e);
        }
    }

    /**
     * 将message发送给JMS中间件
     * 
     * @param message
     *            发送的消息
     * @param config
     *            jms的属性配置项目
     * @return 发送是否成功标志
     * @throws JMSException
     */
    protected boolean sendJmsMessage(Message message, JmsConfig config) throws JMSException {
        try {
            config.start();
            Session session = config.getSession();
            ObjectMessage om = session.createObjectMessage();
            om.setObject(message);
            config.getMessageProducer().send(om);
        } finally {
            config.stop();
        }
        return true;
    }

    /**
     * JMS连接工厂, 连接工厂一般采用{@linkplain javax.annotation.Resource}注解注入
     */
    protected abstract ConnectionFactory getConnectionFactory();

    /**
     * JMS的目的地, 目的地一般采用{@linkplain javax.annotation.Resource}注解注入
     */
    protected abstract Destination getDestination();

    /**
     * 创建jms配置项
     */
    protected JmsConfig createJmsConfig() {
        return new SimpleJmsConfig(getConnectionFactory(), getDestination());
    }

}
