package com.harmony.umbrella.log.support;

import com.harmony.umbrella.log.Message;
import com.harmony.umbrella.log.MessageFactory;

/**
 * @author wuxii@foxmail.com
 */
public class SimpleMessageFactory implements MessageFactory {

    public static final SimpleMessageFactory INSTANCE = new SimpleMessageFactory();

    @Override
    public Message newMessage(Object message) {
        if (message instanceof Throwable) {
            return new SimpleMessage(null, message.toString(), null, (Throwable) message);
        }
        return newMessage((String) message);
    }

    @Override
    public Message newMessage(String message) {
        return new SimpleMessage(message);
    }

    @Override
    public Message newMessage(String message, Object... params) {
        if (params.length == 0) {
            return newMessage(message);
        }
        Throwable throwable = null;
        if (params[params.length - 1] instanceof Throwable) {
            throwable = (Throwable) params[params.length - 1];
        }
        return new SimpleMessage(message, params, throwable);
    }

}
