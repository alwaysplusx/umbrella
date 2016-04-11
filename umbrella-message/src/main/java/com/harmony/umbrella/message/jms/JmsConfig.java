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
 * 
 * <b>另配置信息是有生命周期的, start开始, stop结束.并可以循环使用</b>
 * 
 * @author wuxii@foxmail.com
 */
public interface JmsConfig {

    void start() throws JMSException;

    void stop() throws JMSException;

    Connection getConnection() throws JMSException;

    Session getSession() throws JMSException;

    MessageProducer getMessageProducer() throws JMSException;

    MessageConsumer getMessageConsumer() throws JMSException;

    ConnectionFactory getConnectionFactory();

    Destination getDestination();

}
