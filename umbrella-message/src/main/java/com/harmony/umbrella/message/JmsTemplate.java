package com.harmony.umbrella.message;

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
public interface JmsTemplate {

    boolean isStated();

    void start() throws JMSException;

    void stop() throws JMSException;

    Connection getConnection() throws JMSException;

    Session getSession() throws JMSException;

    MessageProducer getMessageProducer() throws JMSException;

    MessageConsumer getMessageConsumer() throws JMSException;

    ConnectionFactory getConnectionFactory();

    Destination getDestination();

    void commit() throws JMSException;

    void rollback() throws JMSException;
}
