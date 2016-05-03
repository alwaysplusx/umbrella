package com.harmony.umbrella.ws.ext;

import com.harmony.umbrella.ws.cxf.interceptor.MessageOutInterceptor;

/**
 * @author wuxii@foxmail.com
 */
public class LogOutInterceptor extends MessageOutInterceptor {

    public LogOutInterceptor() {
        this.handler = new HarmonyLogMessageHandler();
    }

}
