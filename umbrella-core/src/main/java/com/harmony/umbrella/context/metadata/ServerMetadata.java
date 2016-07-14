package com.harmony.umbrella.context.metadata;

import javax.servlet.ServletContext;

/**
 * 应用的web服务信息
 * 
 * @author wuxii@foxmail.com
 */
public final class ServerMetadata {

    static final ServerMetadata EMPTY_SERVER_METADATA = new ServerMetadata();

    public static final int UNKNOW = 0;
    public static final int WEBLOGIC = 1;
    public static final int WEBSPHERE = 2;
    public static final int GLASSFISH = 3;
    public static final int JBOSS = 4;
    public static final int TOMCAT = 5;

    /**
     * servlet 的版本e.g:3.1
     */
    public final String servletVersion;
    /**
     * web application server 的服务名e.g:Tomcat
     */
    public final String serverName;

    /**
     * server的类型
     */
    public final int serverType;

    public ServerMetadata(ServletContext context) {
        this.serverName = context.getServerInfo();
        this.servletVersion = context.getMajorVersion() + "." + context.getMinorVersion();
        this.serverType = serverType(serverName);
    }

    private ServerMetadata() {
        this.serverName = "";
        this.servletVersion = "";
        this.serverType = UNKNOW;
    }

    private int serverType(String serverName) {
        serverName = serverName.toLowerCase();
        if (serverName.indexOf("weblogic") != -1) {
            return WEBLOGIC;
        } else if (serverName.indexOf("websphere") != -1) {
            return WEBSPHERE;
        } else if (serverName.indexOf("glassfish") != -1) {
            return GLASSFISH;
        } else if (serverName.indexOf("jboss") != -1) {
            return JBOSS;
        } else if (serverName.indexOf("tomcat") != -1) {
            return TOMCAT;
        }
        return UNKNOW;
    }

}