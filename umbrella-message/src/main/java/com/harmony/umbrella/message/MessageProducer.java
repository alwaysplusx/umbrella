package com.harmony.umbrella.message;

/**
 * 消息发送工具接口
 * 
 * @author wuxii@foxmail.com
 */
public interface MessageProducer {

    /**
     * 发送消息给消息中心(JMS, NIO)
     *
     * @param message
     *            需要发送的消息
     * @see javax.jms.MessageProducer#send(javax.jms.Message)
     */
    void send(Message message) throws MessageException;

}
