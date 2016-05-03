package com.harmony.umbrella.message;

import com.harmony.umbrella.util.GenericUtils;

/**
 * 消息解析抽象类
 *
 * @param <T>
 *            实际的message类型
 * @author wuxii@foxmail.com
 */
public abstract class TypedMessageResolver<T extends Message> implements MessageResolver {

    private Class<T> messageType;

    public TypedMessageResolver() {
    }

    public TypedMessageResolver(Class<T> messageType) {
        this.messageType = messageType;
    }

    @Override
    public boolean support(Message message) {
        return getMessageType() == message.getClass();
    }

    @Override
    public void resolve(Message message) {
        process(convert(message));
    }

    /**
     * 处理解析后的消息
     *
     * @param message
     *            待处理的消息
     */
    public abstract void process(T message);

    @SuppressWarnings("unchecked")
    protected Class<T> getMessageType() {
        if (messageType == null) {
            messageType = (Class<T>) GenericUtils.getTargetGeneric(getClass(), TypedMessageResolver.class, 0);
        }
        return messageType;
    }

    /**
     * 将消息解析为对应的实际内容
     *
     * @param message
     *            待解析的消息
     * @return 解析后的消息
     */
    protected T convert(Message message) {
        Class<T> messageType = getMessageType();
        if (messageType.isInstance(message)) {
            return messageType.cast(message);
        }
        throw new IllegalStateException("illegal message type " + message);
    }
}
