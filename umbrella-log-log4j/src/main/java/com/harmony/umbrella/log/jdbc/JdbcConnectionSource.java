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
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author wuxii@foxmail.com
 */
public class JdbcConnectionSource implements ConnectionSource {

    private final String url;

    private final Properties props = new Properties();

    public JdbcConnectionSource(String url, String user, String password) {
        this(url, user, password, new Properties());
    }

    public JdbcConnectionSource(String url, String user, String password, Properties props) {
        this.url = url;
        this.props.putAll(props);
        this.props.put("user", user);
        this.props.put("password", password);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, props);
    }

}
