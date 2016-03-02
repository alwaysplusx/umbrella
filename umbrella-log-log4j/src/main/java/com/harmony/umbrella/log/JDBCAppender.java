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
package com.harmony.umbrella.log;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

import com.harmony.umbrella.util.Exceptions;

/**
 * @author wuxii@foxmail.com
 */
public class JDBCAppender extends AppenderSkeleton {

    private static final Map<String, Method> columnMap = new HashMap<String, Method>(20);

    private boolean initialize;
    private boolean upperCase;
    private String tableName;
    private String columnNames;

    private String url;
    private String user;
    private String password;

    private String sqlStatement;
    private List<Column> columns;

    static {
        Method[] methods = LogInfo.class.getMethods();
        for (Method method : methods) {
            if (method.getDeclaringClass() != Object.class && method.getName().startsWith("get")) {
                columnMap.put(getSampleName(method), method);
            }
        }
    }

    @Override
    protected void append(LoggingEvent event) {
        init();
        if (event.getMessage() instanceof LogInfo) {
            try {
                writeInternal((LogInfo) event.getMessage(), event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    protected void closeConnection(Connection connection) throws SQLException {
        connection.close();
    }

    private void init() {
        if (!initialize) {
            synchronized (this) {
                if (!initialize) {
                    Set<String> names = new HashSet<String>();
                    if ("all".equals(this.columnNames)) {
                        names.addAll(columnMap.keySet());
                    } else {
                        String[] array = columnNames.split(",");
                        for (String name : array) {
                            names.add(name.trim());
                        }
                    }

                    List<String> sortNames = new ArrayList<String>(names);

                    Collections.sort(sortNames, new Comparator<String>() {
                        @Override
                        public int compare(String o1, String o2) {
                            return o1.compareTo(o2);
                        }
                    });

                    columns = new ArrayList<Column>(20);
                    StringBuilder columnPart = new StringBuilder();
                    StringBuilder valuePart = new StringBuilder();

                    Iterator<String> it = sortNames.iterator();

                    int index = 1;
                    while (it.hasNext()) {
                        String name = it.next();
                        if (columnMap.containsKey(name)) {

                            columnPart.append(upperCase ? name.toUpperCase() : name);
                            valuePart.append("?");

                            columns.add(new Column(columnMap.get(name), name, index++, false, false));

                            if (it.hasNext()) {
                                columnPart.append(", ");
                                valuePart.append(", ");
                            }

                        }
                    }
                    this.sqlStatement = "INSERT INTO " + tableName + "(" + columnPart + ") VALUES (" + valuePart + ")";
                    this.initialize = true;
                }
            }
        }
    }

    protected String getSqlStatement() {
        return sqlStatement;
    }

    private void writeInternal(LogInfo logInfo, LoggingEvent event) throws Exception {
        Connection conn = getConnection();
        String sql = getSqlStatement();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        
        for (Column column : getColumns()) {

            Object value = column.getProperty(logInfo);

            column.insertValue(pstmt, value);

        }

        pstmt.execute();
    }

    public List<Column> getColumns() {
        return this.columns;
    }

    public void setColumnNames(String columnNames) {
        this.columnNames = columnNames;
    }

    @Override
    public boolean requiresLayout() {
        return true;
    }

    @Override
    public void close() {
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setUpperCase(boolean upperCase) {
        this.upperCase = upperCase;
    }

    private static String getSampleName(Method method) {
        String name = method.getName().substring(3);
        return Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    static class Column {

        final String name;
        final Method method;
        final int index;
        final boolean isClob;
        final boolean isTimestamp;
        final Class<?> returnType;

        public Column(Method method, String name, int index, boolean isClob, boolean isTimestamp) {
            this.method = method;
            this.returnType = method.getReturnType();
            this.name = name;
            this.index = index;
            this.isClob = isClob;
            this.isTimestamp = isTimestamp;
        }

        public void insertValue(PreparedStatement pstmt, Object value) throws SQLException {
            if (value instanceof String) {
                pstmt.setString(index, (String) value);
            } else if (value instanceof Message) {
                pstmt.setString(index, ((Message) value).getFormattedMessage());
            } else if (value instanceof Throwable) {
                pstmt.setString(index, Exceptions.getRootCause((Throwable) value).toString());
            } else if (value instanceof Level) {
                pstmt.setString(index, ((Level) value).getName());
            } else if (value instanceof Long) {
                pstmt.setLong(index, (Long) value);
            } else if (value instanceof Boolean) {
                pstmt.setBoolean(index, (Boolean) value);
            } else {
                pstmt.setString(index, value == null ? "" : value.toString());
            }
        }

        public Object getProperty(LogInfo logInfo) throws Exception {
            return method.invoke(logInfo);
        }

    }

}
