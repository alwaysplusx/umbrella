package com.harmony.umbrella.message.creator;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import com.harmony.umbrella.message.MessageType;

/**
 * @author wuxii@foxmail.com
 */
public class TextMessageCreator extends AbstractMessageCreator<TextMessage> {

    private static final long serialVersionUID = -2792527853518523027L;
    protected String text;

    public TextMessageCreator(String text) {
        super(MessageType.TextMessage);
        this.text = text;
    }

    @Override
    protected void doMapping(TextMessage message) throws JMSException {
        message.setText(text);
    }

}
