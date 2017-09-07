package com.harmony.umbrella.message.creator;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;

import com.harmony.umbrella.message.MessageTemplate.MessageAppender;

/**
 * @author wuxii@foxmail.com
 */
public class TextMessageCreator extends AbstractMessageCreator<TextMessage> {

    private static final long serialVersionUID = -2792527853518523027L;
    private String text;

    public TextMessageCreator(String text, MessageAppender<TextMessage> appender) {
        super(appender);
        this.text = text;
    }

    @Override
    protected void doMapping(TextMessage message) throws JMSException {
        message.setText(text);
    }

    @Override
    protected TextMessage createMessage(Session session) throws JMSException {
        return session.createTextMessage();
    }

}
