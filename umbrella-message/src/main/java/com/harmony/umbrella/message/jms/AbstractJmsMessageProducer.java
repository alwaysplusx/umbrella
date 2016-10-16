package com.harmony.umbrella.message.jms;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.message.Message;
import com.harmony.umbrella.message.MessageException;

/**
 * 基于JMS的消息发送基础抽象类
 * 
 * @author wuxii@foxmail.com
 */
public abstract class AbstractJmsMessageProducer implements JmsMessageProducer {

    private static final Log log = Logs.getLog(AbstractJmsMessageProducer.class);

    @Override
    public void send(Message message) throws MessageException {
        send(message, createJmsConfig());
    }

    public void send(Message message, JmsConfig config) throws MessageException {
        try {
            sendJmsMessage(message, config);
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
    protected void sendJmsMessage(Message message, JmsConfig config) throws JMSException {
        try {
            config.start();
            Session session = config.getSession();
            ObjectMessage om = session.createObjectMessage();
            om.setObject(message);
            config.getMessageProducer().send(om);
        } finally {
            try {
                config.stop();
            } catch (Exception e) {
                log.error("close jms config exception", e);
            }
        }
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
