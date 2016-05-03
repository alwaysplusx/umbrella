package com.harmony.umbrella.message.jms;

import com.harmony.umbrella.message.Message;
import com.harmony.umbrella.message.MessageException;
import com.harmony.umbrella.message.MessageSender;

/**
 * jms消费生产扩展
 * 
 * @author wuxii@foxmail.com
 */
public interface JmsMessageSender extends MessageSender {

    /**
     * JMS生产/发送消息
     * 
     * @param message
     *            待发送的消息
     * @param config
     *            jms配置项
     * @return
     * @throws MessageException
     */
    boolean send(Message message, JmsConfig config) throws MessageException;

}
