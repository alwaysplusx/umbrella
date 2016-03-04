/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
