package com.harmony.umbrella.message;

/**
 * 监听消息
 *
 * @author wuxii@foxmail.com
 * @see javax.jms.MessageListener
 */
public interface MessageListener {

    /**
     * 资源初始化
     */
    void init();

    /**
     * 当消息到达时候触发
     *
     * @param message
     *            接受的消息
     * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
     */
    void onMessage(Message message);

    /**
     * 清空占用的资源
     */
    void destroy();

}
