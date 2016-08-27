package com.harmony.umbrella.context.metadata;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletContext;

import com.harmony.umbrella.context.metadata.DatabaseMetadata.ConnectionSource;
import com.harmony.umbrella.io.Resource;

/**
 * @author wuxii@foxmail.com
 */
public final class ApplicationMetadata {

    public static final ServerMetadata EMPTY_SERVER_METADATA = ServerMetadata.EMPTY_SERVER_METADATA;
    public static final DatabaseMetadata EMPTY_DATABASE_METADATA = DatabaseMetadata.EMPTY_DATABASE_METADATA;

    private static final JavaMetadata JAVA_METADATA = new JavaMetadata();
    private static final OperatingSystemMetadata OPERATING_SYSTEM_METADATA = new OperatingSystemMetadata();

    public static JavaMetadata getJavaMetadata() {
        return JAVA_METADATA;
    }

    public static OperatingSystemMetadata getOperatingSystemMetadata() {
        return OPERATING_SYSTEM_METADATA;
    }

    public static DatabaseMetadata getDatabaseMetadata(ConnectionSource connectionSource) throws SQLException {
        return new DatabaseMetadata(connectionSource);
    }

    public static ServerMetadata getServerMetadata(ServletContext servletContext) {
        return new ServerMetadata(servletContext);
    }

    public static boolean isApplicationModuleResource(Resource resource) {
        try {
            return resource.getURL().getPath().endsWith("META-INF/MANIFEST.MF");
        } catch (IOException e) {
            return false;
        }
    }

    public static String getApplicationModuleVendor(Class<?> clazz) {
        return clazz.getPackage().getImplementationVendor();
    }

    public static String getApplicationModuleName(Class<?> clazz) {
        return clazz.getPackage().getImplementationTitle();
    }

    public static String getApplicationModuleVersion(Class<?> clazz) {
        return clazz.getPackage().getImplementationVersion();
    }

}
