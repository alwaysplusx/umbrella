package com.harmony.umbrella.message.tracker;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import com.harmony.umbrella.message.MessageTemplate.MessageAppender;
import com.harmony.umbrella.message.MessageTemplate.MessageConfiger;

/**
 * @author wuxii@foxmail.com
 */
public abstract class AbstractMessageConfiger<T extends Message> implements MessageConfiger {

    private static final long serialVersionUID = -4262070889661021925L;

    protected final MessageAppender<T> messageAppender;

    protected boolean beforeMappingDoAppend;

    public AbstractMessageConfiger(MessageAppender<T> appender) {
        this.messageAppender = appender;
    }

    @Override
    public Message message(Session session) throws JMSException {
        T message = createMessage(session);
        if (beforeMappingDoAppend && messageAppender != null) {
            messageAppender.append(message);
        }
        doMapping(message);
        if (!beforeMappingDoAppend && messageAppender != null) {
            messageAppender.append(message);
        }
        return message;
    }

    protected abstract void doMapping(T message) throws JMSException;

    protected abstract T createMessage(Session session) throws JMSException;

    public MessageAppender<T> getMessageAppender() {
        return messageAppender;
    }

    public boolean isBeforeMappingDoAppend() {
        return beforeMappingDoAppend;
    }

    public void setBeforeMappingDoAppend(boolean beforeMappingDoAppend) {
        this.beforeMappingDoAppend = beforeMappingDoAppend;
    }

}
