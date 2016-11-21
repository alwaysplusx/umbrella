package com.harmony.umbrella.context.metadata;

import javax.servlet.ServletContext;

/**
 * 应用的web服务信息
 * 
 * <table border="2" rules="all" cellpadding="4">
 * <thead>
 * <tr>
 * <th align="center" colspan="3">服务类型对照表</th>
 * </tr>
 * </thead> <tbody>
 * <tr>
 * <th>Server</th>
 * <th>Server Type</th>
 * <th>Server Name</th>
 * </tr>
 * <tr>
 * <td>Weblogic</td>
 * <td>1</td>
 * <td>weblogic</td>
 * </tr>
 * <tr>
 * <td>WebSphere</td>
 * <td>2</td>
 * <td>websphere</td>
 * </tr>
 * <tr>
 * <td>Glassfish</td>
 * <td>3</td>
 * <td>glassfish</td>
 * </tr>
 * <tr>
 * <td>JBoss</td>
 * <td>4</td>
 * <td>jboss</td>
 * </tr>
 * <tr>
 * <td>Tomcat</td>
 * <td>5</td>
 * <td>tomcat</td>
 * </tr>
 * <tr>
 * <td>WildFly</td>
 * <td>6</td>
 * <td>wildfly</td>
 * </tr>
 * <tr>
 * <td>其他</td>
 * <td>0</td>
 * <td>unknow</td>
 * </tr>
 * </tbody>
 * </table>
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
    public static final int WILDFLY = 6;

    /**
     * servlet 的版本e.g:3.1
     */
    public final String servletVersion;
    /**
     * web application server 的服务名e.g:tomcat. 名称小写
     */
    public final String serverName;
    /**
     * 服务信息
     */
    public final String serverInfo;
    /**
     * server的类型
     */
    public final int serverType;
    /**
     * 服务的上下文路径
     */
    public final String contextPath;

    public ServerMetadata(ServletContext context) {
        this.serverInfo = context.getServerInfo();
        this.serverName = getServerName(serverInfo);
        this.serverType = serverType(serverName);
        this.servletVersion = context.getMajorVersion() + "." + context.getMinorVersion();
        this.contextPath = context.getContextPath();
    }

    private ServerMetadata() {
        this.serverName = "";
        this.serverInfo = "";
        this.servletVersion = "";
        this.contextPath = "";
        this.serverType = UNKNOW;
    }

    private String getServerName(String serverName) {
        serverName = serverName.toLowerCase();
        if (serverName.indexOf("weblogic") != -1) {
            return "weblogic";
        } else if (serverName.indexOf("websphere") != -1) {
            return "websphere";
        } else if (serverName.indexOf("glassfish") != -1) {
            return "glassfish";
        } else if (serverName.indexOf("jboss") != -1) {
            return "jboss";
        } else if (serverName.indexOf("tomcat") != -1) {
            return "tomcat";
        } else if (serverName.indexOf("wildfly") != -1) {
            return "wildfly";
        }
        return "unknow";
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
        } else if (serverName.indexOf("wildfly") != -1) {
            return WILDFLY;
        }
        return UNKNOW;
    }

}