package com.harmony.umbrella.context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * http的用户操作上下文
 * <p/>
 * scope current< param < request < session < cookie
 *
 * @author wuxii@foxmail.com
 */
public interface HttpCurrentContext extends CurrentContext {

    /**
     * 当前的字符集
     *
     * @return 字符集
     */
    String getCharacterEncoding();

    /**
     * 当前的http请求
     *
     * @return http-request
     */
    HttpServletRequest getHttpRequest();

    /**
     * 当前的http应答
     *
     * @return http-response
     */
    HttpServletResponse getHttpResponse();

    /**
     * 当前环境中是否已经创建了http-session
     *
     * @return if {@code true} has been created
     */
    boolean sessionCreated();

    /**
     * 获取当前的http-session
     *
     * @return http-session
     */
    HttpSession getHttpSession();

    /**
     * 获取session的id
     *
     * @return session id
     */
    String getSessionId();

}
