package com.harmony.umbrella.context;

import java.util.Locale;
import java.util.TimeZone;

/**
 * 用户所能操作信息以及用户的信息将会在保存在{@linkplain CurrentContext}中
 *
 * @author wuxii@foxmail.com
 */
public interface CurrentContext {

    class CurrentContextHolder {
        private static final ThreadLocal<CurrentContext> INSTANCE = new InheritableThreadLocal<>();
    }

    static CurrentContext get() {
        return CurrentContextHolder.INSTANCE.get();
    }

    static void set(CurrentContext currentContext) {
        CurrentContextHolder.INSTANCE.set(currentContext);
    }

    /**
     * 当前用户所对应的用户主体
     *
     * @return 用户主体
     */
    CurrentUser getPrincipals();

    /**
     * 客户端所在的时区
     *
     * @return
     */
    TimeZone getTimeZone();

    /**
     * 用户请求的字符集
     *
     * @return locale
     */
    Locale getLocale();

    /**
     * 当前context对应的请求信息, 如http请求: <code>
     * HttpServletRequest request = cc.getRequest(HttpServletRequest.class);
     * </code>
     *
     * @return current request
     */
    <T> T getRequest(Class<T> type);

    /**
     * 当前context对应的应答信息, 如http请求的应答: {@code HttpServletResponse response = cc.getResponse(HttpServletResponse.class)}
     *
     * @return current resposne
     */
    <T> T getResponse(Class<T> type);

}
