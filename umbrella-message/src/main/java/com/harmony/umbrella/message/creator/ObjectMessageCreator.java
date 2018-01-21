package com.harmony.umbrella.message.creator;

import java.io.Serializable;

import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Session;

/**
 * @author wuxii@foxmail.com
 */
public class ObjectMessageCreator extends AbstractMessageCreator<ObjectMessage> {

    private static final long serialVersionUID = -4357148380966416521L;
    protected Serializable object;

    public ObjectMessageCreator(Serializable object) {
        this.object = object;
    }

    @Override
    protected void doMapping(ObjectMessage message) throws JMSException {
        message.setObject(object);
    }

    @Override
    protected ObjectMessage createMessage(Session session) throws JMSException {
        return session.createObjectMessage();
    }

    @Override
    protected ObjectMessage createMessage(JMSContext jmsContext) throws JMSException {
        return jmsContext.createObjectMessage();
    }

}
