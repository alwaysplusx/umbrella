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

import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.xml.UnrecognizedElementHandler;
import org.w3c.dom.Element;

import com.harmony.umbrella.log.Level;
import com.harmony.umbrella.log.LogInfo;
import com.harmony.umbrella.log.LoggingException;
import com.harmony.umbrella.log.Message;
import com.harmony.umbrella.util.Exceptions;
import com.harmony.umbrella.util.Formats;

/**
 * @author wuxii@foxmail.com
 */
public class JdbcAppender extends AppenderSkeleton implements UnrecognizedElementHandler {

    private static final Map<String, Method> columnMap = new HashMap<String, Method>(20);

    private String tableName;
    private String columnNames;
    private int bufferSize = 1;
    private boolean upperCase;
    private boolean unlistedFieldAutoMatch;
    private boolean dateToString;
    private String dateFormat;

    private String url;
    private String user;
    private String password;

    private String jndiName;

    // 根据配置生成的信息
    private boolean initialize;
    private String sqlStatement;
    private List<Column> columns;

    private DatabaseManager<LoggingEvent> manager;

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
        if (event.getMessage() instanceof LogInfo) {
            init();
            try {
                manager.write((LogInfo) event.getMessage(), event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void init() {
        if (!initialize) {
            synchronized (this) {
                if (!initialize) {
                    // 根据配置的信息得出字段
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

                    // build sql, and column
                    columns = new ArrayList<Column>(sortNames.size());
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

                    // init database manater
                    if (jndiName != null) {
                        this.manager = new JndiDatabaseManager(bufferSize, jndiName, columns, sqlStatement);
                    } else if (url != null && user != null && password != null) {
                        this.manager = new JdbcDatabaseManager(bufferSize, url, user, password, columns, sqlStatement);
                    } else {
                        throw new LoggingException("init database manager fialied");
                    }

                    this.manager.startup();

                    this.initialize = true;
                }
            }
        }
    }

    @Override
    public boolean requiresLayout() {
        return true;
    }

    @Override
    public void close() {
    }

    public void setUnlistedFieldAutoMatch(boolean unlistedFieldAutoMatch) {
        this.unlistedFieldAutoMatch = unlistedFieldAutoMatch;
    }

    public void setDateToString(boolean dateToString) {
        this.dateToString = dateToString;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public void setJndiName(String jndiName) {
        this.jndiName = jndiName;
    }

    public void setColumnNames(String columnNames) {
        this.columnNames = columnNames;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setUpperCase(boolean upperCase) {
        this.upperCase = upperCase;
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

    private static String getSampleName(Method method) {
        String name = method.getName().substring(3);
        return Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }

    class Column {

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

        public void setValue(PreparedStatement pstmt, Object value) throws SQLException {
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
            } else if (value instanceof Date || value instanceof Calendar) {
                if (dateToString) {
                    value = Formats.createDateFormat(dateFormat).format(value);
                    pstmt.setString(index, (String) value);
                } else if (value instanceof Date) {
                    pstmt.setTimestamp(index, new Timestamp(((Date) value).getTime()));
                } else if (value instanceof Calendar) {
                    pstmt.setTimestamp(index, new Timestamp(((Calendar) value).getTimeInMillis()));
                }
            } else {
                pstmt.setString(index, value == null ? "" : value.toString());
            }
        }

        public Object getProperty(LogInfo logInfo) throws Exception {
            return method.invoke(logInfo);
        }

    }

    private static class ColumnElement {
        final String sourceField;
        final String targetField;

        public ColumnElement(String sourceField, String targetField) {
            this.sourceField = sourceField;
            this.targetField = targetField;
        }
    }

    private Set<ColumnElement> columnElements = new HashSet<ColumnElement>();

    @Override
    public boolean parseUnrecognizedElement(Element element, Properties props) throws Exception {
        String tagName = element.getTagName();
        if ("column".equals(tagName)) {
            String sourceField = element.getAttribute("sourceField");
            String targetField = element.getAttribute("targetField");
            columnElements.add(new ColumnElement(sourceField, targetField));
            return true;
        }
        return false;
    }
}
