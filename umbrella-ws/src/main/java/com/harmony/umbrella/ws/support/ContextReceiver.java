package com.harmony.umbrella.ws.support;

import com.harmony.umbrella.ws.Context;

/**
 * 执行信号接收者
 * 
 * @author wuxii@foxmail.com
 *
 */
public interface ContextReceiver {

    /**
     * 接收执行信号
     * 
     * @param context
     *            执行上下文
     */
    void receive(Context context);

}
