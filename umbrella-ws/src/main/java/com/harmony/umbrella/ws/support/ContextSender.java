package com.harmony.umbrella.ws.support;

import com.harmony.umbrella.ws.Context;

/**
 * 执行信号发送者
 *
 * @author wuxii@foxmail.com
 */
public interface ContextSender {

    /**
     * 发送执行信号
     *
     * @param context
     *            执行上下文
     * @return true发送成功, false发送失败
     */
    boolean send(Context context);

}
