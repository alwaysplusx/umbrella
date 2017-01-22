package com.harmony.umbrella.message.tracker;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Session;

import com.harmony.umbrella.message.MessageHelper.MessageAppender;

/**
 * @author wuxii@foxmail.com
 */
public class BytesMessageConfiger extends AbstractMessageConfiger<BytesMessage> {

    /**
     * 
     */
    private static final long serialVersionUID = 3410905359933302863L;
    private byte[] buf;

    public BytesMessageConfiger(byte[] buf, MessageAppender<BytesMessage> appender) {
        super(appender);
        this.buf = buf;
    }

    @Override
    protected void doMapping(BytesMessage message) throws JMSException {
        message.writeBytes(buf);
    }

    @Override
    protected BytesMessage createMessage(Session session) throws JMSException {
        return session.createBytesMessage();
    }

}
