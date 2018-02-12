package com.harmony.umbrella.message.creator;

import java.io.IOException;
import java.io.InputStream;

import javax.jms.JMSException;
import javax.jms.StreamMessage;

import com.harmony.umbrella.message.MessageType;

/**
 * @author wuxii@foxmail.com
 */
public class StreamMessageCreator extends AbstractMessageCreator<StreamMessage> {

    private static final long serialVersionUID = -1513563775655520426L;

    protected InputStream is;

    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    public StreamMessageCreator(InputStream is) {
        super(MessageType.StreamMessage);
        this.is = is;
    }

    @Override
    protected void doMapping(StreamMessage message) throws JMSException {
        byte[] buf = new byte[DEFAULT_BUFFER_SIZE];
        try {
            int index = is.read(buf);
            while (index != -1) {
                message.writeBytes(buf);
                index = is.read(buf);
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

}
