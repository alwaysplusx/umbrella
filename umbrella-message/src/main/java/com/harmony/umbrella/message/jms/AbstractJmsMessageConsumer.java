package com.harmony.umbrella.message.jms;

import java.io.Serializable;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;

import com.harmony.umbrella.message.Message;
import com.harmony.umbrella.message.MessageException;

/**
 * @author wuxii@foxmail.com
 */
public abstract class AbstractJmsMessageConsumer implements JmsMessageConsumer {

    @Override
    public Message consome() throws MessageException {
        return consome(createJmsTemplate(), DEFAULT_RECEIVE_TIMEOUT);
    }

    @Override
    public Message consome(JmsTemplate jmsTemplate) throws MessageException {
        return consome(jmsTemplate, DEFAULT_RECEIVE_TIMEOUT);
    }

    @Override
    public Message consome(JmsTemplate jmsTemplate, long timeout) throws MessageException {
        try {
            javax.jms.Message jmsMessage = consmeJmsMessage(jmsTemplate, timeout);
            if (jmsMessage == null) {
                return null;
            }
            if (jmsMessage instanceof ObjectMessage) {
                Serializable message = ((ObjectMessage) jmsMessage).getObject();
                if (message instanceof Message) {
                    // 唯一正确的消息
                    return (Message) message;
                }
                // 如果接收到不是指定类型的消息抛出异常依赖容器回滚该事务
                // 无法服务转为 com.harmony.umbrella.message.Message
                throw new MessageException("message is not " + Message.class.getName() + " instance");
            }
            // 不是ObjectMessage对象
            throw new MessageException("message is not " + ObjectMessage.class.getName() + " instance");
        } catch (JMSException e) {
            throw new MessageException(e);
        }
    }

    @Override
    public javax.jms.Message consmeJmsMessage(JmsTemplate jmsTemplate, long timeout) throws JMSException {
        try {
            jmsTemplate.start();
            MessageConsumer consumer = jmsTemplate.getMessageConsumer();
            return timeout < 0 ? consumer.receiveNoWait() : consumer.receive(timeout);
        } finally {
            jmsTemplate.stop();
        }
    }

    protected JmsTemplate createJmsTemplate() {
        return new SimpleJmsTemplate(getConnectionFactory(), getDestination());
    }

    /**
     * JMS连接工厂
     * 
     * @return
     */
    protected abstract ConnectionFactory getConnectionFactory();

    /**
     * JMS的目的地
     * 
     * @return
     */
    protected abstract Destination getDestination();

}
