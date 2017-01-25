package com.harmony.umbrella.message;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Map;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;

/**
 * @author wuxii@foxmail.com
 */
public interface MessageTemplate {

    JmsTemplate getJmsTemplate();

    void sendBytesMessage(byte[] buf) throws JMSException;

    void sendBytesMessage(byte[] buf, MessageAppender<BytesMessage> appender) throws JMSException;

    void sendObjectMessage(Serializable obj) throws JMSException;

    void sendObjectMessage(Serializable obj, MessageAppender<ObjectMessage> appender) throws JMSException;

    void sendMapMessage(Map map) throws JMSException;

    void sendMapMessage(Map map, boolean skipNotStatisfiedEntry) throws JMSException;

    void sendMapMessage(Map map, MessageAppender<MapMessage> appender) throws JMSException;

    void sendMapMessage(Map map, boolean skipNotStatisfiedEntry, MessageAppender<MapMessage> appender) throws JMSException;

    void sendTextMessage(String text) throws JMSException;

    void sendTextMessage(String text, MessageAppender<TextMessage> appender) throws JMSException;

    void sendStreamMessage(InputStream is) throws JMSException;

    void sendStreamMessage(InputStream is, MessageAppender<StreamMessage> appender) throws JMSException;

    void sendMessage(MessageConfiger configer) throws JMSException;

    Message receiveMessage() throws JMSException;

    Message receiveMessage(long timeout) throws JMSException;

    void setMessageListener(MessageListener listener);

    MessageListener getMessageListener();

    void startMessageListener() throws JMSException;

    void stopMessageListener() throws JMSException;

    void stopMessageListener(boolean remove) throws JMSException;

    MessageTrackers getMessageTrackers();

    public interface MessageConfiger extends Serializable {
        Message message(Session session) throws JMSException;
    }

    public interface MessageAppender<T extends Message> extends Serializable {
        void append(T message);
    }

}
