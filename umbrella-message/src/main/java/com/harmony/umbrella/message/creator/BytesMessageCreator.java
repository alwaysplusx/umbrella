package com.harmony.umbrella.message.creator;

import javax.jms.BytesMessage;
import javax.jms.JMSException;

import com.harmony.umbrella.message.MessageType;

/**
 * @author wuxii@foxmail.com
 */
public class BytesMessageCreator extends AbstractMessageCreator<BytesMessage> {

    private static final long serialVersionUID = 3410905359933302863L;
    protected byte[] buf;

    public BytesMessageCreator(byte[] buf) {
        super(MessageType.BytesMessage);
        this.buf = buf;
    }

    @Override
    protected void doMapping(BytesMessage message) throws JMSException {
        message.writeBytes(buf);
    }

}
