package com.harmony.umbrella.message;

import java.util.List;

import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;

/**
 * @author wuxii@foxmail.com
 */
public abstract class AbstractMessageListener implements MessageListener {

    protected static final Log LOG = Logs.getLog(AbstractMessageListener.class);

    public AbstractMessageListener() {
    }

    protected abstract List<MessageResolver> getMessageResolvers();

    /**
     * 接受到的消息, 使用{@linkplain MessageResolver#support(Message)}判定当前有哪些是符合条件的
     * {@linkplain MessageResolver}. 再经由
     * {@linkplain MessageResolver#handle(Message)}处理该消息.
     * <p/>
     * <p>
     * 消息是可以被多个{@linkplain MessageResolver}按顺序处理的
     *
     * @see MessageResolver
     */
    @Override
    public void onMessage(Message message) {
        for (MessageResolver mr : getMessageResolvers()) {
            if (mr.support(message)) {
                LOG.debug("{}处理消息{}", mr, message);
                mr.resolve(message);
                return;
            }
        }
        throw new IllegalStateException(message + " no suitable message resolver");
    }

}
