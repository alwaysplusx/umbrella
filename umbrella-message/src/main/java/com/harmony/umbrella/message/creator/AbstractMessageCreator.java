package com.harmony.umbrella.message.creator;

import java.io.Serializable;

import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import com.harmony.umbrella.message.MessageException;
import com.harmony.umbrella.message.MessageTemplate.MessageCreator;

/**
 * @author wuxii@foxmail.com
 */
public abstract class AbstractMessageCreator<T extends Message> implements MessageCreator, Serializable {

    private static final long serialVersionUID = -4262070889661021925L;

    @Override
    public Message newMessage(Session session) {
        try {
            T message = createMessage(session);
            doMapping(message);
            return message;
        } catch (JMSException e) {
            throw new MessageException("message create failed", e);
        }
    }

    @Override
    public Message newMessage(JMSContext jmsContext) {
        try {
            T message = createMessage(jmsContext);
            doMapping(message);
            return message;
        } catch (JMSException e) {
            throw new MessageException("message create failed", e);
        }
    }

    protected abstract void doMapping(T message) throws JMSException;

    protected abstract T createMessage(Session session) throws JMSException;

    protected abstract T createMessage(JMSContext jmsContext) throws JMSException;

}
