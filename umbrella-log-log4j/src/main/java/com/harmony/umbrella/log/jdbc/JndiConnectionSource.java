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
import java.sql.SQLException;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * @author wuxii@foxmail.com
 */
public class JndiConnectionSource implements ConnectionSource {

    private final String jndiName;
    private final Properties props = new Properties();

    private DataSource dataSource;

    public JndiConnectionSource(String jndiName) {
        this(jndiName, new Properties());
    }

    public JndiConnectionSource(String jndiName, Properties props) {
        this.jndiName = jndiName;
        this.props.putAll(props);
    }

    @Override
    public Connection getConnection() throws SQLException {
        try {
            return getDataSource().getConnection();
        } catch (NamingException e) {
            throw new SQLException("jndi not find", e);
        }
    }

    private DataSource getDataSource() throws NamingException {
        if (dataSource == null) {
            InitialContext ctx = new InitialContext(props);
            Object obj = ctx.lookup(this.jndiName);
            if (obj instanceof DataSource) {
                dataSource = (DataSource) obj;
            }
        }
        return dataSource;
    }
}
