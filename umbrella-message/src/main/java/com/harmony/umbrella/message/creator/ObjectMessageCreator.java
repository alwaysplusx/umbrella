package com.harmony.umbrella.message.creator;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import com.harmony.umbrella.message.MessageType;

/**
 * @author wuxii@foxmail.com
 */
public class ObjectMessageCreator extends AbstractMessageCreator<ObjectMessage> {

    private static final long serialVersionUID = -4357148380966416521L;
    protected Serializable object;

    public ObjectMessageCreator(Serializable object) {
        super(MessageType.ObjectMessage);
        this.object = object;
    }

    @Override
    protected void doMapping(ObjectMessage message) throws JMSException {
        message.setObject(object);
    }

}
