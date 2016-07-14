package com.harmony.umbrella.context.metadata;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * 应用所使用的数据库信息
 * 
 * @author wuxii@foxmail.com
 */
public final class DatabaseMetadata {

    static final DatabaseMetadata EMPTY_DATABASE_METADATA = new DatabaseMetadata();

    /**
     * 数据库的名称
     */
    public final String productName;
    /**
     * 数据库的版本
     */
    public final String productVersion;
    /**
     * 数据库的url
     */
    public final String url;
    /**
     * 数据库的用户名
     */
    public final String userName;
    /**
     * 驱动名称
     */
    public final String driverName;
    /**
     * 驱动版本
     */
    public final String driverVersion;
    /**
     * 数据库类型
     */
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

    public DatabaseMetadata(Connection connection) throws SQLException {
        DatabaseMetaData dbmd = connection.getMetaData();
        this.productName = dbmd.getDatabaseProductName();
        this.productVersion = dbmd.getDatabaseProductVersion();
        this.url = dbmd.getURL();
        this.userName = dbmd.getUserName();
        this.driverName = dbmd.getDriverVersion();
        this.driverVersion = dbmd.getDriverVersion();
        this.databaseType = databaseType(productName);
    }

    private DatabaseMetadata() {
        this.productName = "";
        this.productVersion = "";
        this.url = "";
        this.userName = "";
        this.driverName = "";
        this.driverVersion = "";
        this.databaseType = UNKNOW;
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

}