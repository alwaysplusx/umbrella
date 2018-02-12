package com.harmony.umbrella.message;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;

import com.harmony.umbrella.message.MessageTemplate.MessageCreator;

/**
 * @author wuxii@foxmail.com
 */
public enum MessageType implements MessageSerializer, MessageDeserialize, MessageCreator {

    BytesMessage(javax.jms.BytesMessage.class) {

        @Override
        public Serializable serialize(Message message) throws JMSException {
            BytesMessage bytesMessage = (BytesMessage) message;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024 * 4];
            int index = bytesMessage.readBytes(buf);
            while (index != -1) {
                baos.write(buf, 0, index);
                index = bytesMessage.readBytes(buf);
            }
            byte[] o = baos.toByteArray();
            bytesMessage.clearBody();
            bytesMessage.writeBytes(o);
            return o;
        }

        @Override
        public void deserialize(Message message, Serializable content) throws JMSException {
            byte[] o = (byte[]) content;
            BytesMessage bytesMessage = (BytesMessage) message;
            bytesMessage.writeBytes(o);
        }

        @Override
        public Message createMessage(Session session) throws JMSException {
            return session.createBytesMessage();
        }
    },
    TextMessage(javax.jms.TextMessage.class) {

        @Override
        public Serializable serialize(Message message) throws JMSException {
            return ((TextMessage) message).getText();
        }

        @Override
        public void deserialize(Message message, Serializable content) throws JMSException {
            ((TextMessage) message).setText((String) content);
        }

        @Override
        public Message createMessage(Session session) throws JMSException {
            return session.createTextMessage();
        }
    }, //
    ObjectMessage(javax.jms.ObjectMessage.class) {

        @Override
        public Serializable serialize(Message message) throws JMSException {
            return ((ObjectMessage) message).getObject();
        }

        @Override
        public void deserialize(Message message, Serializable content) throws JMSException {
            ((ObjectMessage) message).setObject(content);
        }

        @Override
        public Message createMessage(Session session) throws JMSException {
            return session.createObjectMessage();
        }
    }, //
    StreamMessage(javax.jms.StreamMessage.class) {

        @Override
        public Serializable serialize(Message message) throws JMSException {
            StreamMessage streamMessage = (StreamMessage) message;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024 * 4];
            int index = streamMessage.readBytes(buf);
            while (index != -1) {
                baos.write(buf, 0, index);
                index = streamMessage.readBytes(buf);
            }
            byte[] o = baos.toByteArray();
            streamMessage.clearBody();
            streamMessage.writeBytes(o);
            return o;
        }

        @Override
        public void deserialize(Message message, Serializable content) throws JMSException {
            byte[] o = (byte[]) content;
            StreamMessage streamMessage = (StreamMessage) message;
            streamMessage.writeBytes(o);
        }

        @Override
        public Message createMessage(Session session) throws JMSException {
            return session.createStreamMessage();
        }
    }, //
    MapMessage(javax.jms.MapMessage.class) {

        @Override
        public Serializable serialize(Message message) throws JMSException {
            MapMessage mapMessage = (MapMessage) message;
            Map map = new HashMap<>();
            Enumeration names = mapMessage.getMapNames();
            while (names.hasMoreElements()) {
                Object name = names.nextElement();
                map.put(name, mapMessage.getObject((String) name));
            }
            return (Serializable) map;
        }

        @Override
        public void deserialize(Message message, Serializable content) throws JMSException {
            Map map = (Map) content;
            MapMessage mapMessage = (javax.jms.MapMessage) message;
            for (Object name : map.keySet()) {
                mapMessage.setObject((String) name, map.get(name));
            }
        }

        @Override
        public Message createMessage(Session session) throws JMSException {
            return session.createMapMessage();
        }
    };

    private Class<? extends Message> messageType;

    private MessageType(Class<? extends Message> messageType) {
        this.messageType = messageType;
    }

    public Class<? extends Message> messageType() {
        return messageType;
    }

    public static MessageType forName(String name) {
        for (MessageType t : values()) {
            if (t.toString().equals(name)) {
                return t;
            }
        }
        throw new IllegalArgumentException("message type not found " + name);
    }

    public static MessageType forName(Class<? extends Message> type) {
        for (MessageType t : values()) {
            if (t.messageType.isAssignableFrom(type)) {
                return t;
            }
        }
        throw new IllegalArgumentException("message type not found " + type);
    }

    public static Serializable serializeMessage(Message message) throws JMSException {
        return forName(message.getClass()).serialize(message);
    }

    public static void deserializeMessage(Message message, Serializable content) throws JMSException {
        forName(message.getClass()).deserialize(message, content);
    }
}