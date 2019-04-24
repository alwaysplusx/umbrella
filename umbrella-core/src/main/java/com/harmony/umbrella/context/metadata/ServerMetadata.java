package com.harmony.umbrella.context.metadata;

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

    public static final int UNKNOWS = 0;
    public static final int WEBLOGIC = 1;
    public static final int WEBSPHERE = 2;
    public static final int GLASSFISH = 3;
    public static final int JBOSS = 4;
    public static final int TOMCAT = 5;
    public static final int WILDFLY = 6;
    public static final int JETTY = 7;
    public static final int NETTY = 8;

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

    public ServerMetadata(String servletVersion, String serverName, String serverInfo, int serverType, String contextPath) {
        this.servletVersion = servletVersion;
        this.serverName = serverName;
        this.serverInfo = serverInfo;
        this.serverType = serverType;
        this.contextPath = contextPath;
    }

    private ServerMetadata() {
        this.serverName = "";
        this.serverInfo = "";
        this.servletVersion = "";
        this.contextPath = "";
        this.serverType = UNKNOWS;
    }


}