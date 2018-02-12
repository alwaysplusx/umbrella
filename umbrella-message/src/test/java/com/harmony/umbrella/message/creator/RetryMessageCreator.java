package com.harmony.umbrella.message.creator;

import java.io.Serializable;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import com.harmony.umbrella.message.MessageType;

/**
 * @author wuxii@foxmail.com
 */
public class RetryMessageCreator extends AbstractMessageCreator<Message> {

    private static final long serialVersionUID = -8888260213666255818L;

    private MessageType type;
    private Object content;
    private Map<String, Object> headers;

    public RetryMessageCreator(MessageType type, Object content, Map<String, Object> headers) {
        super(type);
        this.headers = headers;
        this.content = content;
        this.type = type;
    }

    @Override
    protected void doMapping(Message message) throws JMSException {
        type.deserialize(message, (Serializable) content);
        for (String key : headers.keySet()) {
            message.setObjectProperty(key, headers.get(key));
        }
    }

    @Override
    protected Message doCreateMessage(Session session) throws JMSException {
        return type.createMessage(session);
    }

}
