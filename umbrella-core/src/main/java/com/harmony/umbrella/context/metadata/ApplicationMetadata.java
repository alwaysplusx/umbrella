package com.harmony.umbrella.context.metadata;

import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;

/**
 * @author wuxii@foxmail.com
 */
public final class ApplicationMetadata {

    private static final JavaMetadata JAVA_METADATA = new JavaMetadata();
    private static final OperatingSystemMetadata OPERATING_SYSTEM_METADATA = new OperatingSystemMetadata();

    public static JavaMetadata getJavaMetadata() {
        return JAVA_METADATA;
    }

    public static OperatingSystemMetadata getOperatingSystemMetadata() {
        return OPERATING_SYSTEM_METADATA;
    }

    public static DatabaseMetadata getDatabaseMetadata(Connection connection) throws SQLException {
        return new DatabaseMetadata(connection);
    }

    public static ServerMetadata getServerMetadata(ServletContext servletContext) {
        return new ServerMetadata(servletContext);
    }

}
