package com.harmony.umbrella.message.creator;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import com.harmony.umbrella.message.MessageTemplate.MessageCreator;
import com.harmony.umbrella.message.MessageType;

/**
 * @author wuxii@foxmail.com
 */
public abstract class AbstractMessageCreator<T extends Message> implements MessageCreator, Serializable {

    private static final long serialVersionUID = -4262070889661021925L;

    protected final MessageType messageType;

    public AbstractMessageCreator(MessageType messageType) {
        if (messageType == null) {
            throw new IllegalArgumentException("message type must not null");
        }
        this.messageType = messageType;
    }

    @Override
    public Message createMessage(Session session) throws JMSException {
        T message = doCreateMessage(session);
        doMapping(message);
        return message;
    }

    protected abstract void doMapping(T message) throws JMSException;

    protected T doCreateMessage(Session session) throws JMSException {
        return (T) messageType.createMessage(session);
    }

    public MessageType getMessageType() {
        return messageType;
    }

}
