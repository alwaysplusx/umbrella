package com.harmony.umbrella.message.tracker;

import java.io.IOException;
import java.io.InputStream;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.StreamMessage;

import com.harmony.umbrella.message.MessageHelper.MessageAppender;

/**
 * @author wuxii@foxmail.com
 */
public class StreamMessageConfiger extends AbstractMessageConfiger<StreamMessage> {

    /**
     * 
     */
    private static final long serialVersionUID = -1513563775655520426L;

    private InputStream is;

    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    public StreamMessageConfiger(InputStream is, MessageAppender<StreamMessage> appender) {
        super(appender);
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

    @Override
    protected StreamMessage createMessage(Session session) throws JMSException {
        return session.createStreamMessage();
    }

}
