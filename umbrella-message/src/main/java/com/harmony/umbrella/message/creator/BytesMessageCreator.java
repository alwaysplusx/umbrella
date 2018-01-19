package com.harmony.umbrella.message.creator;

import javax.jms.BytesMessage;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Session;

/**
 * @author wuxii@foxmail.com
 */
public class BytesMessageCreator extends AbstractMessageCreator<BytesMessage> {

    private static final long serialVersionUID = 3410905359933302863L;
    private byte[] buf;

    public BytesMessageCreator(byte[] buf) {
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

    @Override
    protected BytesMessage createMessage(JMSContext jmsContext) throws JMSException {
        return jmsContext.createBytesMessage();
    }

}
