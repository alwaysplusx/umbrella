package com.harmony.umbrella.context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.harmony.umbrella.context.metadata.ApplicationMetadata;
import com.harmony.umbrella.context.metadata.ServerMetadata;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;

/**
 * 上下文的助手类
 * 
 * @author wuxii@foxmail.com
 */
public class ContextHelper {

    private static final Log log = Logs.getLog(ContextHelper.class);

    /**
     * 获取应用程序名称
     * 
     * @return 应用程序名称
     */
    public static String getApplicationName() {
        try {
            ApplicationConfiguration cfg = ApplicationContext.getApplicationConfiguration();
            return cfg != null ? cfg.getApplicationName() : null;
        } catch (ApplicationContextException e) {
            return null;
        }
    }

    /**
     * 获取应用的上下文
     * 
     * @return application context
     */
    public static ApplicationContext getApplicationContext() {
        return ApplicationContext.getApplicationContext();
    }

    /**
     * 获取当前服务器的元信息
     * 
     * @return server metadata
     */
    public static ServerMetadata getServerMetadata() {
        try {
            return ApplicationContext.getServerMetadata();
        } catch (ApplicationContextException e) {
            return ApplicationMetadata.EMPTY_SERVER_METADATA;
        }
    }

    // current scope

    /**
     * 获取用户环境的上下文
     * 
     * @return user current context
     */
    public static CurrentContext getCurrentContext() {
        CurrentContext cc = ApplicationContext.getCurrentContext();
        if (cc == null) {
            if (log.isDebugEnabled()) {
                log.warn("application not contain current context, please see {} for more detail", AbstractCurrentContextFilter.class.getName());
            }
        }
        return cc;
    }

    /**
     * 获取用户线程的http request, 如果未找到用户context返回null
     * 
     * @return user http request
     */
    public static HttpServletRequest getHttpRequest() {
        CurrentContext cc = getCurrentContext();
        return cc != null ? cc.getRequest(HttpServletRequest.class) : null;
    }

    /**
     * 获取用户当前线程的http response, 如果未找到用户context返回null
     * 
     * @return user http response
     */
    public static HttpServletResponse getHttpResponse() {
        CurrentContext cc = getCurrentContext();
        return cc != null ? cc.getResponse(HttpServletResponse.class) : null;
    }

    /**
     * 获取用户当前线程的http session, 如果未找到用户context返回null
     * 
     * @return http session
     */
    public static HttpSession getHttpSession() {
        return getHttpSession(true);
    }

    /**
     * 获取当前用户线程中的http session
     * 
     * @param create
     *            session 尚未创建时候自动创建
     * @return http session
     */
    public static HttpSession getHttpSession(boolean create) {
        HttpServletRequest request = getHttpRequest();
        return request != null ? request.getSession(create) : null;
    }

    public static String getRequestUrl() {
        HttpServletRequest request = getHttpRequest();
        return request != null ? getRequestUrl(request) : null;
    }

    // util methods

    public static String getRequestUrl(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri.substring(request.getContextPath().length());
    }

    public static boolean isUnknowServer() {
        return getServerMetadata().serverType == ServerMetadata.UNKNOWS;
    }

    public static boolean isWeblogic() {
        return getServerMetadata().serverType == ServerMetadata.WEBLOGIC;
    }

    public static boolean isWebsphere() {
        return getServerMetadata().serverType == ServerMetadata.WEBSPHERE;
    }

    public static boolean isGlassfish() {
        return getServerMetadata().serverType == ServerMetadata.GLASSFISH;
    }

    public static boolean isJboss() {
        return getServerMetadata().serverType == ServerMetadata.JBOSS;
    }

    public static boolean isWildfly() {
        return getServerMetadata().serverType == ServerMetadata.WILDFLY;
    }

    public static boolean isTomcate() {
        return getServerMetadata().serverType == ServerMetadata.TOMCAT;
    }

}
