package com.harmony.umbrella.message;

import javax.jms.Message;

/**
 * @author wuxii@foxmail.com
 */
public interface MessageTracker {

    /**
     * 准备发送触发
     * <p>
     * <b>能确保此方法在消费前触发</b>
     * 
     * @param message
     *            被发送的消息
     */
    void onBeforeSend(Message message);

    /**
     * 发送成功后触发
     * <p>
     * <b>!!!不能确保此方法在消费前触发</b>
     * 
     * @param message
     *            被发送的消息
     */
    void onAfterSend(Message message);

    /**
     * 发送异常时候触发
     * 
     * @param message
     *            发送的消息
     */
    void onSendException(Message message, Exception ex);

    /**
     * 消费前触发
     * <p>
     * <b>!!!不能确保此方法在发送后触发</b>
     * 
     * @param message
     *            消费的消息
     */
    void onBeforeConsume(Message message);

    /**
     * 消费后触发
     * 
     * @param message
     *            消费的消息
     */
    void onAfterConsume(Message message);

    /**
     * 消费失败时触发
     * 
     * @param message
     *            处理失败的消息
     * @param ex
     *            异常信息
     */
    void onConsumeException(Message message, Exception ex);

}
