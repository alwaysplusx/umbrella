package com.harmony.umbrella.message.tracker;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import com.harmony.umbrella.message.MessageHelper.MessageAppender;

/**
 * @author wuxii@foxmail.com
 */
public class ObjectMessageConfiger extends AbstractMessageConfiger<ObjectMessage> {

    private static final long serialVersionUID = -4357148380966416521L;
    private Serializable object;

    public ObjectMessageConfiger(Serializable object, MessageAppender<ObjectMessage> appender) {
        super(appender);
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

}
