package com.harmony.umbrella.log.message;

import java.io.Serializable;

import com.harmony.umbrella.log.Message;
import com.harmony.umbrella.log.MessageFactory;

public abstract class AbstractMessageFactory implements MessageFactory, Serializable {

    private static final long serialVersionUID = 1L;

    /*
     * (non-Javadoc)
     *
     * @see org.apache.logging.log4j.message.MessageFactory#newMessage(java.lang.Object)
     */
    @Override
    public Message newMessage(final Object message) {
        return new ObjectMessage(message);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.logging.log4j.message.MessageFactory#newMessage(java.lang.String)
     */
    @Override
    public Message newMessage(final String message) {
        return new SimpleMessage(message);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.logging.log4j.message.MessageFactory#newMessage(java.lang.String, java.lang.Object)
     */
    @Override
    public abstract Message newMessage(String message, Object... params);
}