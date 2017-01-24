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
 * 另配置信息是有生命周期的, start开始, stop结束. 并支持循环使用</b>
 * 
 * @author wuxii@foxmail.com
 */
public interface JmsTemplate {

    /**
     * 判断当前的jmsTemplate状态
     * 
     * @return true is started
     */
    boolean isStated();

    /**
     * 启动jmsTemplate
     * 
     * @throws JMSException
     */
    void start() throws JMSException;

    /**
     * 关闭jmsTemplate
     * 
     * @throws JMSException
     */
    void stop() throws JMSException;

    /**
     * 获取当前周期的connection(获取连接器需要先启动jmsTemplate)
     * 
     * @return connection
     * @throws JMSException
     */
    Connection getConnection() throws JMSException;

    /**
     * 获取当前周期的session
     * 
     * @return session
     * @throws JMSException
     */
    Session getSession() throws JMSException;

    /**
     * 获取当前周期的message producer
     * 
     * @return message producer
     * @throws JMSException
     */
    MessageProducer getMessageProducer() throws JMSException;

    /**
     * 获取当前周期的message consumer
     * 
     * @return message consumer
     * @throws JMSException
     */
    MessageConsumer getMessageConsumer() throws JMSException;

    /**
     * 获取jms资源中的connection factory
     * 
     * @return connection factory
     */
    ConnectionFactory getConnectionFactory();

    /**
     * 获取jms资源中的destination
     * 
     * @return destination
     */
    Destination getDestination();

    /**
     * 提交当前session所处理的message会话
     * 
     * @throws JMSException
     */
    void commit() throws JMSException;

    /**
     * 回滚当前session所做的消息处理
     * 
     * @throws JMSException
     */
    void rollback() throws JMSException;
}
