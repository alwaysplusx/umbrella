package com.harmony.umbrella.message.jms;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import com.harmony.umbrella.message.AbstractMessageListener;
import com.harmony.umbrella.util.ReflectionUtils;

/**
 * @author wuxii@foxmail.com
 */
public abstract class AbstractJmsMessageListener extends AbstractMessageListener implements MessageListener {

    /**
     * 当接收消息出现异常后是否将异常回馈给容器
     */
    protected boolean raiseError;

    /**
     * 为JMS提供. 只处理消息类型为{@linkplain com.harmony.umbrella.message.Message}的消息.
     * 如果不为该类型的消息则忽略
     * 
     * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
     */
    @Override
    public void onMessage(javax.jms.Message message) {
        LOG.debug("on message, current message is {}", message);
        Exception ex = null;
        if (message instanceof ObjectMessage) {
            try {
                Serializable object = ((ObjectMessage) message).getObject();
                if (object instanceof com.harmony.umbrella.message.Message) {
                    onMessage((com.harmony.umbrella.message.Message) object);
                    return;
                }
                ex = new IllegalStateException("illegal message type " + object.getClass().getName());
            } catch (JMSException e) {
                ex = e;
            }
        } else {
            ex = new IllegalStateException("jms message is not object message");
        }
        if (ex != null) {
            if (raiseError) {
                // 异常重新抛出给容器
                ReflectionUtils.rethrowRuntimeException(ex);
            } else {
                // 忽略异常
                LOG.error(ex);
            }
        }
    }
}
