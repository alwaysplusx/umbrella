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

    void commit() throws JMSException;

    void rollback() throws JMSException;
}
