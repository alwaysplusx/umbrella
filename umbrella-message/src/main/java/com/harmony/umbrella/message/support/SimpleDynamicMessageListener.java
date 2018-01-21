package com.harmony.umbrella.message.support;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;
import com.harmony.umbrella.message.DynamicMessageListener;
import com.harmony.umbrella.message.JmsTemplate.SessionPoint;
import com.harmony.umbrella.message.MessageException;

/**
 * @author wuxii@foxmail.com
 */
public class SimpleDynamicMessageListener implements DynamicMessageListener {

    private static final Log log = Logs.getLog();

    private MessageListener messageListener;

    private boolean sessionAutoCommit;

    private SessionPoint session;

    public SimpleDynamicMessageListener(SessionPoint session) {
        this.session = session;
    }

    public SimpleDynamicMessageListener(SessionPoint session, boolean autoCommit, MessageListener listener) {
        this.session = session;
        this.messageListener = listener;
        this.sessionAutoCommit = autoCommit;
    }

    public void start() {
        try {
            session.getMessageConsumer().setMessageListener(this);
            log.info("start message listener for {}", session.getDestinationName());
        } catch (JMSException e) {
            throw new MessageException("message listener start failed", e);
        }
    }

    @Override
    public void onMessage(Message message) {
        messageListener.onMessage(message);
        if (sessionAutoCommit) {
            session.commit();
        }
    }

    @Override
    public void stop() throws JMSException {
        log.info("stop {} message listener {}", session.getDestinationName(), messageListener);
        session.release();
        session = null;
    }

    public MessageListener getMessageListener() {
        return messageListener;
    }

    @Override
    public void setMessageListener(MessageListener listener) {
        this.messageListener = listener;
    }

    public boolean isSessionAutoCommit() {
        return sessionAutoCommit;
    }

    public void setSessionAutoCommit(boolean sessionAutoCommit) {
        this.sessionAutoCommit = sessionAutoCommit;
    }

}
