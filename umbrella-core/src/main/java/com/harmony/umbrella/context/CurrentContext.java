package com.harmony.umbrella.context;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.util.Assert;

/**
 * 用户所能操作信息以及用户的信息将会在保存在{@linkplain CurrentContext}中
 *
 * @author wuxii@foxmail.com
 */
public interface CurrentContext {

    /**
     * 用户凭证
     * 
     * @return 用户凭证
     */
    Principals getPrincipals();

    /**
     * 用户端的ip
     * 
     * @return
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
     * 获取当前请求的http上下文
     * 
     * @return http context
     */
    HttpContext getHttpContext();

    /**
     * 当前系统的登录主体
     * 
     * @author wuxii@foxmail.com
     */
    public interface Principals extends Iterable<Object> {

        <T> T getPrincipal(Class<T> type);

        Object getPrincipal(String name);

        List<Object> getPrincipalKeys();

    }

    /**
     * http context
     * 
     * @author wuxii@foxmail.com
     */
    public static final class HttpContext {

        private final HttpServletRequest request;
        private final HttpServletResponse response;

        public HttpContext(HttpServletRequest request, HttpServletResponse response) {
            Assert.notNull(request, "request must not null");
            Assert.notNull(response, "response must not null");
            this.request = request;
            this.response = response;
        }

        /**
         * http request
         *
         * @return http-request
         */
        public HttpServletRequest getHttpRequest() {
            return request;
        }

        /**
         * http response
         *
         * @return http-response
         */
        public HttpServletResponse getHttpResponse() {
            return response;
        }

        /**
         * http session
         *
         * @return http-session
         */
        public HttpSession getHttpSession() {
            return getHttpSession(true);
        }

        /**
         * Returns the current HttpSession associated with this request or, if there is no current session and create is
         * true, returns a new session.
         * 
         * @param create
         * @return
         * @see HttpServletRequest#getSession(boolean)
         */
        public HttpSession getHttpSession(boolean create) {
            return request.getSession(create);
        }

    }

}
