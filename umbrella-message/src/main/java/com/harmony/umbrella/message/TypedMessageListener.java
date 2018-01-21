package com.harmony.umbrella.message;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

/**
 * 此类只有在配合{@linkplain MessageTemplate#startMessageListener(javax.jms.MessageListener)}时候才有效
 * 
 * @author wuxii@foxmail.com
 */
public interface TypedMessageListener<TYPE extends Serializable> extends MessageListener {

    Class<TYPE> getType();

    void handleMessage(TYPE object, Message message);

    @Override
    default void onMessage(Message message) {
        if (message instanceof ObjectMessage) {
            try {
                TYPE object = (TYPE) ((ObjectMessage) message).getObject();
                handleMessage(object, message);
            } catch (JMSException e) {
                throw new MessageException(e);
            }
            return;
        }
        throw new MessageException("TypedMessageListener just works by MessageTemplate#startMessageListener");
    }

}
