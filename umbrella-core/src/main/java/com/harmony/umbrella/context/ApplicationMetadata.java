package com.harmony.umbrella.context;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.servlet.ServletContext;

/**
 * 应用的元信息
 * 
 * @author wuxii@foxmail.com
 */
public class ApplicationMetadata {

    static final ApplicationMetadata METADATA = new ApplicationMetadata();
    
    public static final JVMInformation JVMINFO = METADATA.new JVMInformation();
    
    public static final OSInformation OSINFO = METADATA.new OSInformation();
    
    static final DBInformation EMPTY_DATABASEINFO = METADATA.new DBInformation();
    
    private ApplicationMetadata() {
    }

    /**
     * 应用的web服务信息
     * 
     * @author wuxii@foxmail.com
     */
    public class ServerInformation {

        /**
         * servlet 的版本e.g:3.1
         */
        public final String servletVersion;
        /**
         * web application server 的服务名e.g:Tomcat
         */
        public final String serverName;

        public final int serverType;

        ServerInformation(ServletContext context) {
            this.serverName = context.getServerInfo();
            this.servletVersion = context.getMajorVersion() + "." + context.getMinorVersion();
            this.serverType = serverType(serverName);
        }

        public static final int UNKNOW = 0;
        public static final int WEBLOGIC = 1;
        public static final int WEBSPHERE = 2;
        public static final int GLASSFISH = 3;
        public static final int JBOSS = 4;
        public static final int TOMCAT = 5;

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

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("{servletVersion:");
            builder.append(servletVersion);
            builder.append(", serverName:");
            builder.append(serverName);
            builder.append(", serverType:");
            builder.append(serverType);
            builder.append("}");
            return builder.toString();
        }

    }

    /**
     * 应用所使用的数据库信息
     * 
     * @author wuxii@foxmail.com
     */
    public class DBInformation {

        public final String productName;
        public final String productVersion;
        public final String url;
        public final String userName;
        public final String driverName;
        public final String driverVersion;
        public final int databaseType;

        public static final int UNKNOW = -1;
        public static final int OTHERS = 0;
        public static final int ORACLE = 1;
        public static final int MYSQL = 2;
        public static final int DB2 = 3;
        public static final int H2 = 4;
        public static final int HSQL = 5;
        public static final int SQLSERVER = 6;
        public static final int POSTGRESQL = 7;

        private DBInformation() {
            productName = productVersion = url = userName = driverName = driverVersion = "";
            databaseType = -1;
        }

        DBInformation(Connection connection) throws SQLException {
            DatabaseMetaData dbmd = connection.getMetaData();
            this.productName = dbmd.getDatabaseProductName();
            this.productVersion = dbmd.getDatabaseProductVersion();
            this.url = dbmd.getURL();
            this.userName = dbmd.getUserName();
            this.driverName = dbmd.getDriverVersion();
            this.driverVersion = dbmd.getDriverVersion();
            this.databaseType = databaseType(productName);
        }

        private final int databaseType(String databaseName) {
            databaseName = databaseName.toLowerCase();
            if (databaseName == null) {
                return UNKNOW;
            } else if (databaseName.indexOf("oracle") != -1) {
                return ORACLE;
            } else if (databaseName.indexOf("postgresql") != -1) {
                return POSTGRESQL;
            } else if (databaseName.indexOf("db2") != -1) {
                return DB2;
            } else if (databaseName.indexOf("sql server") != -1) {
                return SQLSERVER;
            } else if (databaseName.indexOf("mysql") != -1) {
                return MYSQL;
            } else if (databaseName.indexOf("hsql") != -1) {
                return HSQL;
            } else if (databaseName.indexOf("h2") != -1) {
                return H2;
            }
            return OTHERS;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("{productName:");
            builder.append(productName);
            builder.append(", productVersion:");
            builder.append(productVersion);
            builder.append(", url:");
            builder.append(url);
            builder.append(", userName:");
            builder.append(userName);
            builder.append(", driverName:");
            builder.append(driverName);
            builder.append(", driverVersion:");
            builder.append(driverVersion);
            builder.append(", databaseType:");
            builder.append(databaseType);
            builder.append("}");
            return builder.toString();
        }

    }

    private static String getSystemProperty(String key) {
        return System.getProperty(key);
    }

    /**
     * 应用所使用的jvm属性
     * 
     * @author wuxii@foxmail.com
     */
    public class JVMInformation {

        public final String specificationName;
        public final String specificationVersion;
        public final String runtimeName;
        public final String runtimeVersion;
        public final String vmName;
        public final String vmVersion;
        public final String vmVendor;
        public final String classVersion;
        public final String libraryPath;
        public final String classPath;
        public final String javaVersion;
        public final String javaHome;
        public final String javaVendor;

        private JVMInformation() {
            this.specificationName = getSystemProperty("java.specification.name");
            this.specificationVersion = getSystemProperty("java.specification.version");
            this.runtimeName = getSystemProperty("java.runtime.name");
            this.runtimeVersion = getSystemProperty("java.runtime.version");
            this.vmName = getSystemProperty("java.vm.name");
            this.vmVendor = getSystemProperty("java.vm.vendor");
            this.vmVersion = getSystemProperty("java.vm.version");
            this.javaVersion = getSystemProperty("java.version");
            this.javaHome = getSystemProperty("java.home");
            this.javaVendor = getSystemProperty("java.vendor");
            this.classVersion = getSystemProperty("java.class.version");
            this.libraryPath = getSystemProperty("java.library.path");
            this.classPath = getSystemProperty("java.class.path");
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("{specificationName:");
            builder.append(specificationName);
            builder.append(", specificationVersion:");
            builder.append(specificationVersion);
            builder.append(", runtimeName:");
            builder.append(runtimeName);
            builder.append(", runtimeVersion:");
            builder.append(runtimeVersion);
            builder.append(", vmName:");
            builder.append(vmName);
            builder.append(", vmVersion:");
            builder.append(vmVersion);
            builder.append(", vmVendor:");
            builder.append(vmVendor);
            builder.append(", classVersion:");
            builder.append(classVersion);
            builder.append(", libraryPath:");
            builder.append(libraryPath);
            builder.append(", classPath:");
            builder.append(classPath);
            builder.append(", javaVersion:");
            builder.append(javaVersion);
            builder.append(", javaHome:");
            builder.append(javaHome);
            builder.append(", javaVendor:");
            builder.append(javaVendor);
            builder.append("}");
            return builder.toString();
        }

    }

    /**
     * 操作系统信息
     * 
     * @author wuxii@foxmail.com
     */
    public class OSInformation {

        public final String osName;
        public final String osVersion;
        public final String osPatchLevel;
        public final String userHome;
        public final String userName;
        public final String language;
        public final String timeZone;
        public final String fileEncoding;
        public final String fileSeparator;
        public final String cpu;
        public final String cpuModel;

        private OSInformation() {
            this.osName = getSystemProperty("os.name");
            this.osVersion = getSystemProperty("os.version");
            this.osPatchLevel = getSystemProperty("sun.os.patch.level");
            this.userHome = getSystemProperty("user.home");
            this.userName = getSystemProperty("user.name");
            this.language = getSystemProperty("user.language");
            this.timeZone = getSystemProperty("user.timezone");
            this.fileEncoding = getSystemProperty("file.encoding");
            this.fileSeparator = getSystemProperty("file.separator");
            this.cpu = getSystemProperty("sun.cpu.isalist");
            this.cpuModel = getSystemProperty("sun.arch.data.model");
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("{osName:");
            builder.append(osName);
            builder.append(", osVersion:");
            builder.append(osVersion);
            builder.append(", osPatchLevel:");
            builder.append(osPatchLevel);
            builder.append(", userHome:");
            builder.append(userHome);
            builder.append(", userName:");
            builder.append(userName);
            builder.append(", language:");
            builder.append(language);
            builder.append(", timeZone:");
            builder.append(timeZone);
            builder.append(", fileEncoding:");
            builder.append(fileEncoding);
            builder.append(", fileSeparator:");
            builder.append(fileSeparator);
            builder.append(", cpu:");
            builder.append(cpu);
            builder.append(", cpuModel:");
            builder.append(cpuModel);
            builder.append("}");
            return builder.toString();
        }

    }

}
