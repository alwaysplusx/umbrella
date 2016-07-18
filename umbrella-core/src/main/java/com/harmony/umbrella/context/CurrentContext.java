package com.harmony.umbrella.context;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 用户所能操作信息以及用户的信息将会在保存在{@linkplain CurrentContext}中
 *
 * @author wuxii@foxmail.com
 */
public interface CurrentContext extends Serializable {

    /**
     * key: 用户
     */
    String USER = CurrentContext.class.getName() + ".USER";

    /**
     * key:用户id
     */
    String USER_ID = CurrentContext.class.getName() + ".USER_ID";

    /**
     * key:client id
     */
    String CLIENT_ID = CurrentContext.class.getName() + ".CLIENT_ID";

    /**
     * key:用户的名称
     */
    String USER_NAME = CurrentContext.class.getName() + ".USER_NAME";

    /**
     * key:用户别名
     */
    String USER_NICKNAME = CurrentContext.class.getName() + ".USER_NICKNAME";

    /**
     * 自定义用户
     * 
     * @return user
     */
    <T> T getUser();

    /**
     * 用户id
     *
     * @return 用户id
     */
    <T> T getUserId();

    /**
     * 用户名
     *
     * @return 用户名
     */
    String getUsername();

    /**
     * 用户别名
     * 
     * @return 用户别名
     */
    String getNickname();

    /**
     * 用户端的ip
     * 
     * @return
     */
    String getUserHost();

    /**
     * 验证是否登录授权
     * 
     * @return true已经登录
     */
    boolean isAuthenticated();

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
     * 用户上下文中是否包含对应的值
     *
     * @param name
     *            key of value
     */
    boolean containsKey(String name);

    /**
     * 获取{@code name}对应的值， 如果不存在返回{@code null}
     *
     * @param name
     *            key of value
     * @return if not exists return {@code null}
     */
    <T> T get(String name);

    /**
     * 对当前的用户环境设置值
     *
     * @param name
     *            key of value
     * @param o
     *            value
     */
    void put(String name, Object o);

    /**
     * 当前用户环境中包含的key
     *
     * @return 值的枚举类
     */
    Enumeration<String> getCurrentNames();

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
    boolean doesSessionCreated();

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
