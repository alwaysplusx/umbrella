package com.harmony.umbrella.log.jdbc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import com.harmony.umbrella.log.jdbc.JdbcAppender.Column;

/**
 * @author wuxii@foxmail.com
 */
public class JdbcDatabaseManager extends AbstractLog4jDatabaseManager {

    private String url;
    private String user;
    private String password;

    private boolean isBatchSupported;

    public JdbcDatabaseManager(int bufferSize, String url, String user, String password, List<Column> columns, String sqlStatement) {
        super(bufferSize, columns, sqlStatement);
        this.url = url;
        this.user = user;
        this.password = password;
    }

    @Override
    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    @Override
    public void startup() {
        Connection connection = null;
        try {
            connection = getConnection();
            DatabaseMetaData metaData = connection.getMetaData();
            this.isBatchSupported = metaData.supportsBatchUpdates();
        } catch (SQLException e) {
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                }
            }
        }
    }

    @Override
    public boolean isBatchSupported() {
        return isBatchSupported;
    }

    @Override
    public void shutdown() {
    }

}
