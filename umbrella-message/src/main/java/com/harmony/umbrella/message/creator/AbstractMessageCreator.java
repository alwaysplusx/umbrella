package com.harmony.umbrella.message.creator;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import com.harmony.umbrella.message.MessageTemplate.MessageCreator;

/**
 * @author wuxii@foxmail.com
 */
public abstract class AbstractMessageCreator<T extends Message> implements MessageCreator {

    private static final long serialVersionUID = -4262070889661021925L;

    @Override
    public Message message(Session session) throws JMSException {
        T message = createMessage(session);
        doMapping(message);
        return message;
    }

    protected abstract void doMapping(T message) throws JMSException;

    protected abstract T createMessage(Session session) throws JMSException;

}
