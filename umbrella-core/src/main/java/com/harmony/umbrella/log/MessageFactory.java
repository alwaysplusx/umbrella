package com.harmony.umbrella.log;

/**
 * 日志消息创建工厂
 * 
 * @author wuxii@foxmail.com
 */
public interface MessageFactory {
    
    /**
     * Creates a new message based on an Object.
     *
     * @param message
     *            a message object
     * @return a new message
     */
    Message newMessage(Object message);

    /**
     * Creates a new message based on a String.
     *
     * @param message
     *            a message String
     * @return a new message
     */
    Message newMessage(String message);

    /**
     * Creates a new parameterized message.
     *
     * @param message
     *            a message template, the kind of message template depends on
     *            the implementation.
     * @param params
     *            the message parameters
     * @return a new message
     * @see com.harmony.umbrella.log.message.ParameterizedMessageFactory
     */
    Message newMessage(String message, Object... params);

}
