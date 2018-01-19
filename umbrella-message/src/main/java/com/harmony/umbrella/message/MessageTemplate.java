package com.harmony.umbrella.message;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Map;

import javax.jms.Destination;
import javax.jms.JMSContext;
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
     * 发送bytes消息
     * 
     * @param buf
     *            消息内容
     * @throws JMSException
     */
    void sendBytesMessage(byte[] buf) throws JMSException;

    /**
     * 发送序列化消息
     * 
     * @param obj
     *            消息内容
     * @throws JMSException
     */
    void sendObjectMessage(Serializable obj) throws JMSException;

    /**
     * 发送map消息
     * 
     * @param map
     *            消息内容
     * @throws JMSException
     */
    void sendMapMessage(Map map) throws JMSException;

    /**
     * 发送map消息
     * 
     * @param map
     *            消息内容
     * @param skipNotStatisfiedEntry
     *            是否忽略不满足序列化条件的消息entry
     * @throws JMSException
     */
    void sendMapMessage(Map map, boolean skipNotStatisfiedEntry) throws JMSException;

    /**
     * 发送文本消息
     * 
     * @param text
     *            消息内容
     * @throws JMSException
     */
    void sendTextMessage(String text) throws JMSException;

    /**
     * 发送stream消息
     * 
     * @param is
     *            消息内容
     * @throws JMSException
     */
    void sendStreamMessage(InputStream is) throws JMSException;

    /**
     * 发送自定义消息
     * 
     * @param messageCreator
     *            通过creator自定义构建消息主体
     * @throws JMSException
     */
    void sendMessage(MessageCreator messageCreator) throws JMSException;

    /**
     * 向置顶目的地发送消息
     * 
     * @param dest
     *            目的地
     * @param messageCreator
     *            消息的创建者
     * @param configure
     *            消息生产者配置
     */
    void sendMessage(Destination destination, MessageCreator messageCreator, MessageProducerConfigure configure) throws JMSException;

    /**
     * 接收消息
     * 
     * @return 接收到的message
     * @throws JMSException
     */
    Message receiveMessage() throws JMSException;

    /**
     * 接收消息, 如果timeout<0则使用{@linkplain MessageConsumer#receiveNoWait()}.
     * 超时时间timeout=0则阻塞等待接收消息直到有消息可以消费{@linkplain MessageConsumer#receive(long)}
     * 
     * @param timeout
     *            指定超时时间
     * @return 接收到的message
     * @throws JMSException
     */
    Message receiveMessage(long timeout) throws JMSException;

    /**
     * 设置message监听listener
     * 
     * @param listener
     *            message监听
     */
    void setMessageListener(MessageListener listener);

    /**
     * 消息事件监听(发送, 消费等事件), monitor运行在沙盒中, 其发生异常也不影响业务.
     * 
     * @param monitor
     *            事件监听器
     */
    void setMessageMonitor(MessageMonitor monitor);

    /**
     * 获取消息监听listener
     * 
     * @return message listener
     * @throws JMSException
     */
    MessageListener getMessageListener();

    /**
     * 启动消息监听listener
     * 
     * @throws JMSException
     * @throws IllegalStateException
     *             不存在消息监听 or 消息监听listener已经启动
     */
    void startMessageListener() throws JMSException;

    /**
     * 停止消息监听listener
     * 
     * @throws JMSException
     */
    void stopMessageListener() throws JMSException;

    /**
     * 停止消息监听listener
     * 
     * @param remove
     *            是否在停止后移除消息监听listener
     * @throws JMSException
     */
    void stopMessageListener(boolean remove) throws JMSException;

    /**
     * 自定义消息构建器
     * 
     * @author wuxii@foxmail.com
     */
    public interface MessageCreator {

        Message newMessage(Session session) throws JMSException;

        Message newMessage(JMSContext jmsContext) throws JMSException;

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
