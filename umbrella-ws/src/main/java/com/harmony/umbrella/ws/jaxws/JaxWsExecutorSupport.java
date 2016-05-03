package com.harmony.umbrella.ws.jaxws;

import com.harmony.umbrella.ws.Context;

/**
 * 消息发送扩展
 * 
 * @author wuxii@foxmail.com
 */
public interface JaxWsExecutorSupport extends JaxWsExecutor {

    /**
     * 发生消息
     * 
     * @param context
     *            消息上下文
     * @return 消息发生成功标志
     * @see com.harmony.umbrella.ws.support.ContextSender#send(Context)
     */
    boolean send(Context context);

}
