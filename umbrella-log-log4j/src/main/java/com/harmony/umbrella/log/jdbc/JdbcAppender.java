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

import java.io.StringReader;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
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

/**
 * @author wuxii@foxmail.com
 */
public class JdbcAppender extends AppenderSkeleton implements UnrecognizedElementHandler {

    private static final Map<String, Method> logInfoMapper = new HashMap<String, Method>(20);

    private String tableName;
    private int bufferSize = 1;
    private boolean upperCase = false;
    private boolean autoMatchUnlistedField = false;

    private String url;
    private String user;
    private String password;

    private String jndiName;

    // 根据配置生成的信息
    private boolean initialize;
    private String sqlStatement;
    private final List<Column> columns = new ArrayList<Column>();

    private DatabaseManager<LoggingEvent> manager;

    private final Set<ColumnElement> columnElements = new HashSet<ColumnElement>();

    static {
        // 生成LogInfo的信息
        Method[] methods = LogInfo.class.getMethods();
        for (Method method : methods) {
            if (method.getDeclaringClass() != Object.class && method.getName().startsWith("get")) {
                // in xml configuration name is method sample name. (getModule -> module)
                logInfoMapper.put(getSampleName(method), method);
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

                    // 配置LogInfo与表中字段的关系
                    if (autoMatchUnlistedField) {
                        autoMatchUnlistedField();
                    }

                    // 根据columnElement生成与数据库相对于的列以及sql语句
                    initColumn();

                    // 数据存储管理初始化
                    initManager();

                    this.initialize = true;
                }
            }
        }
    }

    private void initManager() {
        ConnectionSource connectionSource = null;

        if (jndiName != null) {
            connectionSource = new JndiConnectionSource(jndiName);
        } else if (url != null && user != null && password != null) {
            connectionSource = new JdbcConnectionSource(url, user, password);
        }

        if (connectionSource == null) {
            throw new LoggingException("connection sources not configuration");
        }

        this.manager = new JdbcDatabaseManager(bufferSize, connectionSource, sqlStatement, columns);
        this.manager.startup();
    }

    private void initColumn() {
        List<ColumnElement> sortedColumnElements = sortColumnElement(columnElements);

        // build sql, and column
        StringBuilder columnPart = new StringBuilder();
        StringBuilder valuePart = new StringBuilder();

        Iterator<ColumnElement> it = sortedColumnElements.iterator();

        int index = 1;
        while (it.hasNext()) {
            ColumnElement ce = it.next();

            if (logInfoMapper.containsKey(ce.sourceField)) {

                columnPart.append(upperCase ? ce.targetField.toUpperCase() : ce.targetField);
                valuePart.append("?");

                this.columns.add(new Column(logInfoMapper.get(ce.sourceField), ce, index++));

                if (it.hasNext()) {
                    columnPart.append(", ");
                    valuePart.append(", ");
                }

            }
        }

        this.sqlStatement = "INSERT INTO " + tableName + "(" + columnPart + ") VALUES (" + valuePart + ")";
    }

    private List<ColumnElement> sortColumnElement(Set<ColumnElement> ce) {
        ArrayList<ColumnElement> list = new ArrayList<ColumnElement>(ce);
        Collections.sort(list, new Comparator<ColumnElement>() {
            @Override
            public int compare(ColumnElement o1, ColumnElement o2) {
                return o1.sourceField.compareTo(o2.sourceField);
            }
        });
        return list;
    }

    private void autoMatchUnlistedField() {
        Set<String> fields = logInfoMapper.keySet();
        for (String field : fields) {
            Method method = logInfoMapper.get(field);
            // 如果返回类型是事件类型默认将时间类型的记录方式定位timestamp
            Class<?> returnType = method.getReturnType();
            String dateType = "NONE";
            if (returnType.isAssignableFrom(Date.class) || returnType.isAssignableFrom(Calendar.class)) {
                dateType = ColumnElement.DATE_TYPE_TIMESTAMP;
            }
            ColumnElement ce = new ColumnElement(field, field, dateType, false);
            if (!columnElements.contains(ce)) {
                columnElements.add(ce);
            }
        }
    }

    @Override
    public boolean requiresLayout() {
        return false;
    }

    @Override
    public void close() {
    }

    public void setAutoMatchUnlistedField(boolean autoMatchUnlistedField) {
        this.autoMatchUnlistedField = autoMatchUnlistedField;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public void setJndiName(String jndiName) {
        this.jndiName = jndiName;
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

    /**
     * 解析配置appender的自定义tag <code>column</code>
     * 
     * <pre>
     *  &lt;appender name="name" class=""&gt;
     *      &lt;column sourceField="" targetField="" /&gt;
     *      &lt;layout class=""&gt;
     *          &lt;param name="" value=""/&gt;        
     *      &lt;/layout&gt;
     *  &lt;appender/&gt;
     * </pre>
     * 
     * @see org.apache.log4j.xml.UnrecognizedElementHandler#parseUnrecognizedElement(org.w3c.dom.Element,
     *      java.util.Properties)
     */
    @Override
    public boolean parseUnrecognizedElement(Element element, Properties props) throws Exception {
        String tagName = element.getTagName();
        if ("column".equals(tagName)) {

            String sourceField = element.getAttribute("sourceField");
            String targetField = element.getAttribute("targetField");
            String dateType = element.getAttribute("dateType");

            if (dateType == null || "".equals(dateType)) {
                dateType = ColumnElement.DATE_TYPE_NONE;
            }

            boolean isClob = Boolean.valueOf(element.getAttribute("isClob"));

            columnElements.add(new ColumnElement(sourceField, targetField, dateType, isClob));
            return true;
        }
        return false;
    }

    private static String getSampleName(Method method) {
        String name = method.getName().substring(3);
        return Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }

    class Column {

        final ColumnElement columnElement;
        final Method method;
        final int index;
        final Class<?> returnType;

        public Column(Method method, ColumnElement ce, int index) {
            this.method = method;
            this.columnElement = ce;
            this.returnType = method.getReturnType();
            this.index = index;
        }

        public void setValue(PreparedStatement pstmt, Object value) throws SQLException {

            if (value == null) {
                pstmt.setNull(index, Types.NULL);

            } else if (value instanceof String) {
                setString(pstmt, (String) value);

            } else if (value instanceof Message) {
                setString(pstmt, ((Message) value).getFormattedMessage());

            } else if (value instanceof Throwable) {
                if (columnElement.isClob) {
                    value = Exceptions.getAllMessage((Throwable) value);
                } else {
                    value = Exceptions.getRootCause((Throwable) value);
                }
                setString(pstmt, String.valueOf(value));

            } else if (value instanceof Level) {
                setString(pstmt, ((Level) value).getName());

            } else if (value instanceof Long) {
                pstmt.setLong(index, (Long) value);

            } else if (value instanceof Boolean) {
                pstmt.setBoolean(index, (Boolean) value);

            } else if (value instanceof Date) {
                setDate(pstmt, ((Date) value).getTime());

            } else if (value instanceof Calendar) {
                setDate(pstmt, ((Calendar) value).getTimeInMillis());

            } else {
                setString(pstmt, String.valueOf(value));

            }
        }

        private void setString(PreparedStatement pstmt, String value) throws SQLException {
            if (columnElement.isClob) {
                pstmt.setClob(index, new StringReader(value));
            } else {
                pstmt.setString(index, value);
            }
        }

        private void setDate(PreparedStatement pstmt, long timeInMillis) throws SQLException {

            if (ColumnElement.DATE_TYPE_NONE.equals(columnElement.dateType) //
                    || ColumnElement.DATE_TYPE_TIMESTAMP.equals(columnElement.dateType)) {
                pstmt.setTimestamp(index, new Timestamp(timeInMillis));

            } else if (ColumnElement.DATE_TYPE_TIME.equals(columnElement.dateType)) {
                pstmt.setTime(index, new Time(timeInMillis));

            } else if (ColumnElement.DATE_TYPE_DATE.equals(columnElement.dateType)) {
                pstmt.setDate(index, new java.sql.Date(timeInMillis));

            }
        }

        public Object getProperty(LogInfo logInfo) throws Exception {
            return method.invoke(logInfo);
        }

    }

    private static class ColumnElement {

        static final String DATE_TYPE_NONE = "NONE";
        static final String DATE_TYPE_TIMESTAMP = "TIMESTAMP";
        static final String DATE_TYPE_TIME = "TIME";
        static final String DATE_TYPE_DATE = "DATE";

        final String sourceField;
        final String targetField;
        final boolean isClob;
        final String dateType;

        public ColumnElement(String sourceField, String targetField, String dateType, boolean isClob) {
            this.sourceField = sourceField;
            this.targetField = targetField;
            this.isClob = isClob;
            this.dateType = dateType;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((sourceField == null) ? 0 : sourceField.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            ColumnElement other = (ColumnElement) obj;
            if (sourceField == null) {
                if (other.sourceField != null)
                    return false;
            } else if (!sourceField.equals(other.sourceField))
                return false;
            return true;
        }
    }

}
