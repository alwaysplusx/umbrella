package com.harmony.umbrella.context.metadata;

import com.harmony.umbrella.core.ConnectionSource;

import javax.servlet.ServletContext;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import static com.harmony.umbrella.context.metadata.ServerMetadata.*;

/**
 * @author wuxii@foxmail.com
 */
public final class ApplicationMetadata {

    public static DataSourceMetadata of(ConnectionSource connectionSource) throws SQLException {
        return new DataSourceMetadata(connectionSource);
    }

    public static ServerMetadata of(ServletContext servletContext) {
        String serverInfo = servletContext.getServerInfo();
        String servletVersion = servletContext.getMajorVersion() + "." + servletContext.getMinorVersion();
        String contextPath = servletContext.getContextPath();
        String serverName = getServerName(serverInfo);
        int serverType = serverType(serverName);
        return new ServerMetadata(servletVersion, serverName, serverInfo, serverType, contextPath);
    }

    private ServerMetadata serverMetadata;
    private List<DataSourceMetadata> dataSourceMetadata;
    private JavaMetadata javaMetadata;
    private OperatingSystemMetadata operatingSystemMetadata;

    public ApplicationMetadata(ServerMetadata serverMetadata,
                               List<DataSourceMetadata> dataSourceMetadata) {
        this(serverMetadata, dataSourceMetadata, JavaMetadata.INSTANCE, OperatingSystemMetadata.INSTANCE);
    }

    public ApplicationMetadata(ServerMetadata serverMetadata, List<DataSourceMetadata> dataSourceMetadata,
                               JavaMetadata javaMetadata, OperatingSystemMetadata operatingSystemMetadata) {
        this.serverMetadata = serverMetadata;
        this.dataSourceMetadata = Collections.unmodifiableList(dataSourceMetadata);
        this.javaMetadata = javaMetadata;
        this.operatingSystemMetadata = operatingSystemMetadata;
    }

    public JavaMetadata getJavaMetadata() {
        return javaMetadata;
    }

    public OperatingSystemMetadata getOperatingSystemMetadata() {
        return operatingSystemMetadata;
    }

    public ServerMetadata getServerMetadata() {
        return serverMetadata;
    }

    public List<DataSourceMetadata> getDataSourceMetadata() {
        return dataSourceMetadata;
    }

    private static String getServerName(String serverName) {
        serverName = serverName.toLowerCase();
        if (serverName.contains("weblogic")) {
            return "weblogic";
        } else if (serverName.contains("websphere")) {
            return "websphere";
        } else if (serverName.contains("glassfish")) {
            return "glassfish";
        } else if (serverName.contains("jboss")) {
            return "jboss";
        } else if (serverName.contains("tomcat")) {
            return "tomcat";
        } else if (serverName.contains("wildfly")) {
            return "wildfly";
        } else if (serverName.contains("jetty")) {
            return "jetty";
        }
        return "unknow";
    }

    private static int serverType(String serverName) {
        serverName = serverName.toLowerCase();
        if (serverName.contains("weblogic")) {
            return WEBLOGIC;
        } else if (serverName.contains("websphere")) {
            return WEBSPHERE;
        } else if (serverName.contains("glassfish")) {
            return GLASSFISH;
        } else if (serverName.contains("jboss")) {
            return JBOSS;
        } else if (serverName.contains("tomcat")) {
            return TOMCAT;
        } else if (serverName.contains("wildfly")) {
            return WILDFLY;
        } else if (serverName.contains("jetty")) {
            return JETTY;
        }
        return UNKNOWS;
    }

}
