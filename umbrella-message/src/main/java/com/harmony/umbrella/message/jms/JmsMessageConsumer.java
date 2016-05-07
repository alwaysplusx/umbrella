package com.harmony.umbrella.message.jms;

import javax.jms.JMSException;

import com.harmony.umbrella.message.Message;
import com.harmony.umbrella.message.MessageConsumer;
import com.harmony.umbrella.message.MessageException;

/**
 * 消息消费JMS扩展
 * 
 * @author wuxii@foxmail.com
 */
public interface JmsMessageConsumer extends MessageConsumer {

    /**
     * 默认消费等待时间按 5s
     */
    int DEFAULT_RECEIVE_TIMEOUT = 1000 * 5;

    /**
     * 通过jms的配置属性消费消息
     * 
     * @param config
     *            jms配置属性
     * @return 消费的消息
     * @throws MessageException
     */
    Message consome(JmsConfig config) throws MessageException;

    /**
     * 通过jms配置项目消费消息并指定消费等待时间
     * <p>
     * timeout<0表示及时消费,如果没有消费到消息返回null
     * 
     * @param config
     *            jms配置项
     * @param timeout
     *            消费等待时间
     * @return 消费的消息
     * @throws MessageException
     * @see {@link javax.jms.MessageConsumer#receiveNoWait()}
     */
    Message consome(JmsConfig config, long timeout) throws MessageException;

    /**
     * 消费jms消息
     * 
     * @param config
     *            jms配置项
     * @param timeout
     *            消费等待时间
     * @return
     * @throws JMSException
     * @see {@link javax.jms.MessageConsumer#receiveNoWait()}
     * @see {@linkplain #consome(JmsConfig, long)}
     */
    javax.jms.Message consmeJmsMessage(JmsConfig config, long timeout) throws JMSException;

}
