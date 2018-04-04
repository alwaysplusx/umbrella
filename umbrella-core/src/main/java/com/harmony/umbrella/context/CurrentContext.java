package com.harmony.umbrella.context;

import java.util.Locale;

/**
 * 用户所能操作信息以及用户的信息将会在保存在{@linkplain CurrentContext}中
 *
 * @author wuxii@foxmail.com
 */
public interface CurrentContext {

    /**
     * 当前用户所对应的用户主体
     * 
     * @return 用户主体
     */
    Object getPrincipals();

    /**
     * 用户端的host
     * 
     * @return request host
     */
    String getHost();

    /**
     * 客户端的本地化
     */
    Locale getLocale();

    /**
     * 设置用户环境的本地化
     *
     * @param locale
     *            {@linkplain Locale}
     */
    void setLocale(Locale locale);

    /**
     * 当前的字符集
     *
     * @return 字符集
     */
    String getCharacterEncoding();

    /**
     * 当前context对应的请求信息, 如http请求: <code>
     *  HttpServletRequest request = cc.getRequest(HttpServletRequest.class);
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
