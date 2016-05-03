package com.harmony.umbrella.message;

/**
 * 消息消费工具
 * 
 * @author wuxii@foxmail.com
 */
public interface MessageConsumer {

    /**
     * 消费消息
     * 
     * @return 消费的消息
     * @throws MessageException
     */
    Message consome() throws MessageException;

}
