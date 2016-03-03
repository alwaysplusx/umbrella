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
import java.sql.SQLException;
import java.util.List;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import com.harmony.umbrella.log.LoggingException;
import com.harmony.umbrella.log.jdbc.JdbcAppender.Column;

/**
 * @author wuxii@foxmail.com
 */
public class JndiDatabaseManager extends AbstractLog4jDatabaseManager {

    private final String jndiName;
    private DataSource dataSource;
    private boolean isBatchSupported;

    public JndiDatabaseManager(int bufferSize, String jndiName, List<Column> columns, String sqlStatement) {
        super(bufferSize, columns, sqlStatement);
        this.jndiName = jndiName;
    }

    @Override
    protected Connection getConnection() throws SQLException {
        return this.dataSource.getConnection();
    }

    public void startup() {
        Connection connection = null;
        try {
            InitialContext ctx = new InitialContext();
            Object obj = ctx.lookup(this.jndiName);
            if (obj instanceof DataSource) {
                connection = dataSource.getConnection();
                DatabaseMetaData metaData = connection.getMetaData();
                this.isBatchSupported = metaData.supportsBatchUpdates();
            } else {
                throw new LoggingException("jndi name [" + jndiName + "] not mapper to datasource");
            }
        } catch (Exception e) {
            throw new LoggingException(e);
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
