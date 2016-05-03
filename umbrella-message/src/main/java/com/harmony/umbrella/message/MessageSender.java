package com.harmony.umbrella.message;

/**
 * 消息发送工具接口
 * 
 * @author wuxii@foxmail.com
 */
public interface MessageSender {

    /**
     * 发送消息给消息中心(JMS, NIO)
     *
     * @param message
     *            需要发送的消息
     * @return if return {@code true} 发送成功
     * @see javax.jms.MessageProducer#send(javax.jms.Message)
     */
    boolean send(Message message) throws MessageException;

}
