package com.harmony.umbrella.message.tracker;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;

import com.harmony.umbrella.message.MessageHelper.MessageAppender;

/**
 * @author wuxii@foxmail.com
 */
public class TextMessageConfiger extends AbstractMessageConfiger<TextMessage> {

    private static final long serialVersionUID = -2792527853518523027L;
    private String text;

    public TextMessageConfiger(String text, MessageAppender<TextMessage> appender) {
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
