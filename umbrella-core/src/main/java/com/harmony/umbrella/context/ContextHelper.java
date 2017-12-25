package com.harmony.umbrella.context;

import static com.harmony.umbrella.context.CurrentContext.USER_ID;
import static com.harmony.umbrella.context.CurrentContext.USER_NAME;
import static com.harmony.umbrella.context.CurrentContext.USER_NICKNAME;

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.util.Assert;

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
     * 获取应用的上下文
     * 
     * @return application context
     */
    public static ApplicationContext getApplicationContext() {
        return ApplicationContext.getApplicationContext();
    }

    /**
     * 获取用户环境的上下文
     * 
     * @return user current context
     */
    public static CurrentContext getCurrentContext() {
        CurrentContext cc = ApplicationContext.getCurrentContext();
        if (cc == null) {
            if (log.isDebugEnabled()) {
                log.warn("application not contain current context, please see {} for more detail", CurrentContextFilter.class.getName());
            }
        }
        return cc;
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

    /**
     * 获取用户线程的http request, 如果未找到用户context返回null
     * 
     * @return user http request
     */
    public static HttpServletRequest getHttpRequest() {
        CurrentContext cc = getCurrentContext();
        return cc != null ? cc.getHttpRequest() : null;
    }

    /**
     * 获取用户当前线程的http response, 如果未找到用户context返回null
     * 
     * @return user http response
     */
    public static HttpServletResponse getHttpResponse() {
        CurrentContext cc = getCurrentContext();
        return cc != null ? cc.getHttpResponse() : null;
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
        CurrentContext cc = getCurrentContext();
        return cc != null ? cc.getHttpSession(create) : null;
    }

    /**
     * 获取当前线程范围的登录用户用户名
     * 
     * @return user name
     */
    public static String getUsername() {
        CurrentContext cc = getCurrentContext();
        return cc != null ? cc.getUsername() : null;
    }

    /**
     * 获取当前线程的用户昵称
     * 
     * @return user nickname
     */
    public static String getNickname() {
        CurrentContext cc = getCurrentContext();
        return cc != null ? cc.getNickname() : null;
    }

    /**
     * 获取当前线程的用户id
     * 
     * @return user id
     */
    public static Long getUserId() {
        CurrentContext cc = getCurrentContext();
        return cc != null ? (Long) cc.getUserId() : null;
    }

    public static String getUserHost() {
        CurrentContext cc = getCurrentContext();
        return cc != null ? cc.getUserHost() : null;
    }

    public static UserInfo getUserInfo() {
        HttpSession session = getHttpSession();
        if (session == null) {
            throw new ApplicationContextException("not http request, can't get http session");
        }
        Map<String, Object> properties = new LinkedHashMap<>();
        Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String attrName = attributeNames.nextElement();
            properties.put(attrName, session.getAttribute(attrName));
        }
        return new UserInfo(getUserId(), getUsername(), getNickname(), properties);
    }

    public static void applyToSession(UserInfo userInfo) throws ApplicationContextException {
        Assert.notNull(userInfo, "user info not allow null");
        HttpSession session = getHttpSession();
        if (session == null) {
            throw new ApplicationContextException("not http request, can't get http session");
        }
        for (Entry<String, Object> entry : userInfo.properties.entrySet()) {
            session.setAttribute(entry.getKey(), entry.getValue());
        }
        session.setAttribute(USER_ID, userInfo.userId);
        session.setAttribute(USER_NAME, userInfo.username);
        session.setAttribute(USER_NICKNAME, userInfo.nickname);
    }

    public static boolean isUnknowServer() {
        return getServerMetadata().serverType == ServerMetadata.UNKNOW;
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

    public static final class UserInfo {

        protected final Long userId;
        protected final String username;
        protected final String nickname;
        protected final Map<String, Object> properties;

        public UserInfo(Long userId, String username, String nickname) {
            this(userId, username, nickname, null);
        }

        public UserInfo(Long userId, String username, String nickname, Map<String, Object> properties) {
            this.userId = userId;
            this.username = username;
            this.nickname = nickname;
            this.properties = properties == null ? Collections.emptyMap() : Collections.unmodifiableMap(properties);
        }

        public Long getUserId() {
            return userId;
        }

        public String getUsername() {
            return username;
        }

        public String getNickname() {
            return nickname;
        }

        public Map<String, Object> getProperties() {
            return properties;
        }

    }
}
