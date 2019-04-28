package com.harmony.umbrella.log.support;

import com.harmony.umbrella.log.Message;
import com.harmony.umbrella.log.MessageFactory;
import org.apache.logging.log4j.message.ParameterizedMessageFactory;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.util.ClassUtils;

/**
 * @author wuxii@foxmail.com
 */
public class MessageFactoryFactoryBean {

    public static MessageFactory getMessageFactory() {
        if (isLog4j2ParameterizedMessageFactoryPresent()) {
            return parameterizedMessageFactory();
        }
        if (isSlf4jMessageFormatterPresent()) {
            return messageFormatterMessageFactory();
        }
        return SimpleMessageFactory.INSTANCE;
    }

    private static boolean isLog4j2ParameterizedMessageFactoryPresent() {
        return ClassUtils.isPresent("org.apache.logging.log4j.message.ParameterizedMessageFactory", ClassUtils.getDefaultClassLoader());
    }

    private static boolean isSlf4jMessageFormatterPresent() {
        return ClassUtils.isPresent("org.slf4j.helpers.MessageFormatter", ClassUtils.getDefaultClassLoader());
    }

    private static MessageFactory messageFormatterMessageFactory() {
        return MessageFormatterMessageFactory.INSTANCE;
    }

    private static MessageFactory parameterizedMessageFactory() {
        return ParameterizedMessageFactoryAdapter.INSTANCE;
    }

    private static class MessageFormatterMessageFactory extends SimpleMessageFactory {

        private static final MessageFormatterMessageFactory INSTANCE = new MessageFormatterMessageFactory();

        @Override
        public Message newMessage(String message, Object... params) {
            if (params.length == 0) {
                return newMessage(message);
            }
            Throwable throwable = null;
            if (params[params.length - 1] instanceof Throwable) {
                throwable = (Throwable) params[params.length - 1];
            }
            return new TupleMessage(message, MessageFormatter.arrayFormat(message, params, throwable));
        }

    }

    private static class TupleMessage implements Message {

        private static final long serialVersionUID = -4157821740018839852L;
        private String format;
        private FormattingTuple tuple;

        private TupleMessage(String format, FormattingTuple tuple) {
            this.format = format;
            this.tuple = tuple;
        }

        @Override
        public Throwable getThrowable() {
            return tuple.getThrowable();
        }

        @Override
        public Object[] getParameters() {
            return tuple.getArgArray();
        }

        @Override
        public String getFormattedMessage() {
            return tuple.getMessage();
        }

        @Override
        public String getFormat() {
            return format;
        }

    }

    private static class MessageAdapter implements Message {

        private static final long serialVersionUID = -2449273194316544217L;
        private org.apache.logging.log4j.message.Message message;

        public MessageAdapter(org.apache.logging.log4j.message.Message message) {
            this.message = message;
        }

        @Override
        public String getFormattedMessage() {
            return message.getFormattedMessage();
        }

        @Override
        public String getFormat() {
            return message.getFormat();
        }

        @Override
        public Object[] getParameters() {
            return message.getParameters();
        }

        @Override
        public Throwable getThrowable() {
            return message.getThrowable();
        }

    }

    private static class ParameterizedMessageFactoryAdapter implements MessageFactory {

        private static final ParameterizedMessageFactoryAdapter INSTANCE = new ParameterizedMessageFactoryAdapter();

        private org.apache.logging.log4j.message.MessageFactory messageFactory = ParameterizedMessageFactory.INSTANCE;

        @Override
        public Message newMessage(Object message) {
            return new MessageAdapter(messageFactory.newMessage(message));
        }

        @Override
        public Message newMessage(String message) {
            return new MessageAdapter(messageFactory.newMessage(message));
        }

        @Override
        public Message newMessage(String message, Object... params) {
            return new MessageAdapter(messageFactory.newMessage(message, params));
        }

    }
}
