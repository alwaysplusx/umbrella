package com.harmony.umbrella.message;

import javax.jms.JMSException;
import javax.jms.MessageListener;

/**
 * @author wuxii@foxmail.com
 */
public interface DynamicMessageListener extends MessageListener {

    boolean isStarted();

    void start() throws JMSException;

    void stop() throws JMSException;

}
