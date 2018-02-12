package com.harmony.umbrella.message;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Map;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;

/**
 * 消息管理模版, 包含一下基础功能
 * <ul>
 * <li>jmsTemplate管理
 * <li>各类型消息发送
 * <li>消息接收
 * <li>消息监听
 * <li>消息追踪
 * </ul>
 * 
 * @author wuxii@foxmail.com
 */
public interface MessageTemplate {

    /**
     * 使用模版发送的消息中会自动带入对应的消息头, 此为消息头对应的前缀
     */
    String TEMPLATE_MESSAGE_PREFIX = "TemplateMessage";

    /**
     * {@link #sendObjectMessage(Serializable)}发送Object Message时候带入的消息头. 便于JMS MessageSelector识别选取消费的消息
     */
    String TEMPLATE_MESSAGE_OBJECT_TYPE = TEMPLATE_MESSAGE_PREFIX + "ObjectType";

    /**
     * 定制的模版消息id
     */
    String TEMPLATE_MESSAGE_ID = TEMPLATE_MESSAGE_PREFIX + "Id";

    /**
     * 发送bytes消息
     * 
     * @param buf
     *            消息内容
     * 
     * @return messageId
     */
    String sendBytesMessage(byte[] buf);

    /**
     * 发送序列化消息
     * 
     * @param obj
     *            消息内容
     * @return messageId
     */
    String sendObjectMessage(Serializable obj);

    /**
     * 发送map消息
     * 
     * @param map
     *            消息内容
     * @return messageId
     */
    String sendMapMessage(Map map);

    /**
     * 发送map消息
     * 
     * @param map
     *            消息内容
     * @param skipNotStatisfiedEntry
     *            是否忽略不满足序列化条件的消息entry
     * @return messageId
     */
    String sendMapMessage(Map map, boolean skipNotStatisfiedEntry);

    /**
     * 发送文本消息
     * 
     * @param text
     *            消息内容
     * @return messageId
     */
    String sendTextMessage(String text);

    /**
     * 发送stream消息
     * 
     * @param is
     *            消息内容
     * @return messageId
     */
    String sendStreamMessage(InputStream is);

    /**
     * 发送自定义消息
     * 
     * @param messageCreator
     *            通过creator自定义构建消息主体
     * @return messageId
     */
    String sendMessage(MessageCreator messageCreator);

    /**
     * 向置顶目的地发送消息
     * 
     * @param dest
     *            目的地
     * @param messageCreator
     *            消息的创建者
     * @param configure
     *            消息生产者配置
     * @return messageId
     */
    String sendMessage(Destination destination, MessageCreator messageCreator, MessageProducerConfigure configure);

    /**
     * 接收消息
     * 
     * @return 接收到的message
     * 
     */
    Message receiveMessage();

    /**
     * 接收指定id的消息
     * 
     * @param messageId
     *            message id
     * @return message
     */
    Message receiveMessage(String messageId);

    /**
     * 接收置顶id的消息
     * 
     * @param messageId
     *            message id
     * @param timeout
     *            timeout
     * @return message
     */
    Message receiveMessage(String messageId, long timeout);

    /**
     * 接收消息, 如果timeout<0则使用{@linkplain MessageConsumer#receiveNoWait()}.
     * 超时时间timeout=0则阻塞等待接收消息直到有消息可以消费{@linkplain MessageConsumer#receive(long)}
     * 
     * @param timeout
     *            指定超时时间
     * @return 接收到的message
     * 
     */
    Message receiveMessage(long timeout);

    /**
     * 接收消息
     * 
     * @param timeout
     *            接收超时事件
     * @param quiet
     *            安静模式, true则不触发对应的messageMonitor
     * @return message
     */
    Message receiveMessage(long timeout, boolean quiet);

    /**
     * 消息事件监听(发送, 消费等事件), monitor运行在沙盒中, 其发生异常也不影响业务.
     * 
     * @param monitor
     *            事件监听器
     */
    void setMessageMonitor(MessageMonitor monitor);

    /**
     * 获取message monitor
     * 
     * @return message monitor
     */
    MessageMonitor getMessageMonitor();

    DynamicMessageListener startMessageListener(MessageListener listener);

    DynamicMessageListener startMessageListener(String messageSelector, MessageListener listener);

    /**
     * jms template
     * 
     * @return jms template
     */
    JmsTemplate getJmsTemplate();

    /**
     * 自定义消息构建器
     * 
     * @author wuxii@foxmail.com
     */
    public interface MessageCreator {

        Message createMessage(Session session) throws JMSException;

    }

    /**
     * 消息生产配置
     * 
     * @author wuxii@foxmail.com
     */
    public interface MessageProducerConfigure {

        void config(MessageProducer producer);

    }

}
