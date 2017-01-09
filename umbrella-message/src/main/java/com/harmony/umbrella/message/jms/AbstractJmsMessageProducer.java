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
        send(message, getJmsTemplate());
    }

    public void send(Message message, JmsTemplate jmsTemplate) throws MessageException {
        try {
            sendJmsMessage(message, jmsTemplate);
        } catch (JMSException e) {
            throw new MessageException(e);
        }
    }

    @Override
    public <T extends javax.jms.Message> void send(MessageConfiger<T> messageConfiger) throws JMSException {
        JmsTemplate jmsTemplate = getJmsTemplate();
        Session session = jmsTemplate.getSession();
        javax.jms.Message message = messageConfiger.create(session);
        jmsTemplate.getMessageProducer().send(message);
    }

    /**
     * 将message发送给JMS中间件
     * 
     * @param message
     *            发送的消息
     * @param jmsTemplate
     *            jms的属性配置项目
     * @return 发送是否成功标志
     * @throws JMSException
     */
    protected void sendJmsMessage(Message message, JmsTemplate jmsTemplate) throws JMSException {
        try {
            jmsTemplate.start();
            Session session = jmsTemplate.getSession();
            ObjectMessage om = session.createObjectMessage();
            om.setObject(message);
            jmsTemplate.getMessageProducer().send(om);
        } finally {
            try {
                jmsTemplate.stop();
            } catch (Exception e) {
                log.error("close jms jmsTemplate exception", e);
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
    protected JmsTemplate getJmsTemplate() {
        return new SimpleJmsTemplate(getConnectionFactory(), getDestination());
    }

}
