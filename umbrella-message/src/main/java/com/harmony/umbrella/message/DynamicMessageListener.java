package com.harmony.umbrella.message;

import javax.jms.JMSException;
import javax.jms.MessageListener;

/**
 * @author wuxii@foxmail.com
 */
public interface DynamicMessageListener extends MessageListener {

    void stop() throws JMSException;

    // dynamic setting

    MessageListener getMessageListener();

    void setMessageListener(MessageListener listener);

}
