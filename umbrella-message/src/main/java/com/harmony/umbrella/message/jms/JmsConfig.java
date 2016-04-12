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

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

/**
 * 通过配置信息加载jms相关的连接, 目的地, session, 消费者, 生产者 等等
 * <p>
 * <b>配置项是线程不安全的,请勿在多线程情况下使用.
 * <p>
 * 另配置信息是有生命周期的, start开始, stop结束. 并可以循环使用</b>
 * 
 * @author wuxii@foxmail.com
 */
public interface JmsConfig {

    /**
     * 配置的开始点
     * 
     * @throws JMSException
     */
    void start() throws JMSException;

    /**
     * 配置的结束点
     * 
     * @throws JMSException
     */
    void stop() throws JMSException;

    /**
     * 获取JMS连接
     */
    Connection getConnection() throws JMSException;

    /**
     * 获取JMS消息会话
     */
    Session getSession() throws JMSException;

    /**
     * 获取jms消息生产者
     */
    MessageProducer getMessageProducer() throws JMSException;

    /**
     * 获取jms消息消费者
     */
    MessageConsumer getMessageConsumer() throws JMSException;

    /**
     * 连接工厂
     */
    ConnectionFactory getConnectionFactory();

    /**
     * jms目的地
     */
    Destination getDestination();

}
