package com.harmony.umbrella.message.jms;

import com.harmony.umbrella.message.MessageException;
import com.harmony.umbrella.message.MessageProducer;

/**
 * jms消费生产扩展
 * 
 * @author wuxii@foxmail.com
 */
public interface JmsMessageProducer extends MessageProducer {

    void send(MessageCreater messageCreater) throws MessageException;

}
