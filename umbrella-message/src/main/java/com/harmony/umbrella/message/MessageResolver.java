package com.harmony.umbrella.message;

/**
 * 消息解析
 *
 * @author wuxii@foxmail.com
 */
public interface MessageResolver {

    /**
     * 验证Message是否支持解析
     *
     * @param message
     *            消息
     * @return if support return {@code true}
     */
    boolean support(Message message);

    /**
     * 处理消息
     *
     * @param message
     *            消息
     */
    void resolve(Message message);

}
