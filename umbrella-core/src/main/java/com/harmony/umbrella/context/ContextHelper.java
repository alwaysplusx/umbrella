package com.harmony.umbrella.context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.harmony.umbrella.context.metadata.DatabaseMetadata;
import com.harmony.umbrella.context.metadata.ServerMetadata;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;

/**
 * @author wuxii@foxmail.com
 */
public class ContextHelper {

    private static final Log log = Logs.getLog(ContextHelper.class);

    public static ApplicationContext getApplicationContext() {
        return ApplicationContext.getApplicationContext();
    }

    public static CurrentContext getCurrentContext() {
        CurrentContext cc = ApplicationContext.getCurrentContext();
        if (cc == null) {
            log.warn("application not contain current context, please see {} for more detail", CurrentContextFilter.class.getName());
        }
        return cc;
    }

    public static ServerMetadata getServerMetadata() {
        return ApplicationContext.getServerMetadata();
    }

    public static DatabaseMetadata[] getDatabaseMetadata() {
        return ApplicationContext.getDatabaseMetadatas();
    }

    public static HttpServletRequest getHttpRequest() {
        CurrentContext cc = getCurrentContext();
        return cc != null ? cc.getHttpRequest() : null;
    }

    public static HttpServletResponse getHttpResponse() {
        CurrentContext cc = getCurrentContext();
        return cc != null ? cc.getHttpResponse() : null;
    }

    public static HttpSession getHttpSession() {
        CurrentContext cc = getCurrentContext();
        return cc != null ? cc.getHttpSession() : null;
    }

    public static String getUsername() {
        CurrentContext cc = getCurrentContext();
        return cc != null ? cc.getUsername() : null;
    }

    public static String getNickname() {
        CurrentContext cc = getCurrentContext();
        return cc != null ? cc.getNickname() : null;
    }

    public static Long getUserId() {
        CurrentContext cc = getCurrentContext();
        return cc != null ? (Long) cc.getUserId() : null;
    }

    public static String getUserHost() {
        CurrentContext cc = getCurrentContext();
        return cc != null ? cc.getUserHost() : null;
    }
}
