package com.harmony.umbrella.ws.ext;

import com.harmony.umbrella.ws.cxf.interceptor.MessageInInterceptor;

/**
 * @author wuxii@foxmail.com
 */
public class LogInInterceptor extends MessageInInterceptor {

    public LogInInterceptor() {
        this.handler = new HarmonyLogMessageHandler();
    }

}
