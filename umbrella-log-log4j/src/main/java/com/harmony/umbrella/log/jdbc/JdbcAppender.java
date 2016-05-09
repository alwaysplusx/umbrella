
package com.harmony.umbrella.log.jdbc;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.xml.UnrecognizedElementHandler;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.harmony.umbrella.log.Level;
import com.harmony.umbrella.log.LogInfo;
import com.harmony.umbrella.log.LoggingException;
import com.harmony.umbrella.log.Message;
import com.harmony.umbrella.log.parser.LogInfoParser;
import com.harmony.umbrella.log.parser.NamedParser;
import com.harmony.umbrella.util.ReflectionUtils;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public class JdbcAppender extends AppenderSkeleton implements UnrecognizedElementHandler {

    private static final Map<String, Method> logInfoMapper = new HashMap<String, Method>(20);
    private static final Map<String, Integer> sqlTypeMap = new HashMap<String, Integer>();
    
    private static final LogInfoParser defaultParser = new NamedParser();
    
    static {
        // 生成LogInfo的信息
        Method[] methods = LogInfo.class.getMethods();
        for (Method method : methods) {
            if (!ReflectionUtils.isObjectMethod(method) && ReflectionUtils.isReadMethod(method)) {
                // in xml configuration name is equal to method sample name. (getModule -> module)
                logInfoMapper.put(getSampleName(method), method);
            }
        }

        Class<?> clazz = java.sql.Types.class;
        for (Field f : clazz.getDeclaredFields()) {
            try {
                sqlTypeMap.put(f.getName(), (Integer) f.get(clazz));
            } catch (Exception e) {
            }
        }
    }

    private static String getSampleName(Method method) {
        String name = method.getName().substring(3);
        return Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }

    // trace info
    // 0     1
    public static int level = -1;

    private String tableName;
    private int bufferSize = 1;
    private boolean upperCase = true;

    private String includeFields;
    private String excludeFields;

    private Set<String> includeSet;
    private Set<String> excludeSet;

    private String url;
    private String user;
    private String password;

    private String jndiName;

    // 根据配置生成的信息
    private boolean initialize;
    private String sqlStatement;
    // 配置的columns
    private final List<ColumnElement> columnElements = new ArrayList<ColumnElement>();
    // 最终解析后的columns
    private final List<Column> columns = new ArrayList<Column>();

    private DatabaseManager<LoggingEvent> manager;

    @Override
    protected void append(LoggingEvent event) {
        // 只有自定义的日志消息才进入数据库，自定义类型为logInfo
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
                    autoMatchField();

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

    /**
     * 配置部分的字段映射，并生成最终的sql语句
     */
    private void initColumn() {
        Collections.sort(columnElements, new Comparator<ColumnElement>() {
            @Override
            public int compare(ColumnElement o1, ColumnElement o2) {
                return o1.source.compareTo(o2.source);
            }
        });

        // build sql, and column
        StringBuilder columnPart = new StringBuilder();
        StringBuilder valuePart = new StringBuilder();

        for (int i = 0, max = columnElements.size(); i < max; i++) {
            ColumnElement ce = columnElements.get(i);

            // 存在于logInfo中的属性
            columnPart.append(upperCase ? ce.target.toUpperCase() : ce.target);
            valuePart.append("?");

            this.columns.add(new Column(ce, i + 1));

            if (i + 1 < max) {
                columnPart.append(", ");
                valuePart.append(", ");
            }

        }

        this.sqlStatement = "INSERT INTO " + (upperCase ? tableName.toLowerCase() : tableName) + "(" + columnPart + ") VALUES (" + valuePart + ")";

        debug("sql statement " + this.sqlStatement);
    }

    /**
     * 自动映射数据库字段与logInfo的关系
     */
    private void autoMatchField() {
        Set<String> fields = logInfoMapper.keySet();
        for (String source : fields) {
            if (isAutoMatchField(source)) {
                Method method = logInfoMapper.get(source);
                // 如果返回类型是事件类型默认将时间类型的记录方式定位timestamp
                Class<?> sourceType = method.getReturnType();
                Integer sqlType = getSQLType(sourceType);
                ColumnElement ce = new ColumnElement(source, sourceType, source, sqlType, defaultParser);
                // 不在配置项内则添加
                if (!columnElements.contains(ce)) {
                    columnElements.add(ce);
                }
            }
        }
    }

    private Integer getSQLType(Class<?> clazz) {
        return null;
    }

    @Override
    public boolean requiresLayout() {
        return false;
    }

    @Override
    public void close() {
    }

    protected boolean isAutoMatchField(String fieldName) {
        return getIncludeFieldSet().contains(fieldName) || !getExcludeFieldSet().contains(fieldName);
    }

    private synchronized Set<String> getIncludeFieldSet() {
        if (includeSet == null) {
            includeSet = new HashSet<String>();
            if (includeFields != null) {
                StringTokenizer st = new StringTokenizer(includeFields, ",");
                while (st.hasMoreTokens()) {
                    includeSet.add(st.nextToken().trim());
                }
            }
        }
        return includeSet;
    }

    private synchronized Set<String> getExcludeFieldSet() {
        if (excludeSet == null) {
            excludeSet = new HashSet<String>();
            if (excludeFields != null) {
                StringTokenizer st = new StringTokenizer(excludeFields, ",");
                while (st.hasMoreTokens()) {
                    excludeSet.add(st.nextToken().trim());
                }
            }
        }
        return excludeSet;
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

    public void setIncludeFields(String includeFields) {
        this.includeFields = includeFields;
    }

    public void setExcludeFields(String excludeFields) {
        this.excludeFields = excludeFields;
    }

    /**
     * 解析配置appender的自定义tag <code>columns</code>
     * <p>
     * 
     * <pre>
     *  &lt;appender name="name" class=""&gt;
     *      &lt;columns&gt;
     *          &lt;column source="module" target="module" /&gt;
     *      &lt;/columns&gt;
     *  &lt;appender/&gt;
     * </pre>
     *
     * @see org.apache.log4j.xml.UnrecognizedElementHandler#parseUnrecognizedElement(org.w3c.dom.Element,
     *      java.util.Properties)
     */
    @Override
    public boolean parseUnrecognizedElement(Element element, Properties props) throws Exception {
        String tagName = element.getTagName();
        if ("columns".equals(tagName) && element.hasChildNodes()) {
            NodeList childNodes = element.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node node = childNodes.item(i);
                if (node instanceof Element) {
                    String childTagName = ((Element) node).getTagName();
                    if ("column".equals(childTagName)) {
                        parseColumn((Element) node);
                        continue;
                    }
                    info("unrecognized child element inner columns " + childTagName);
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 将配置文件中的column解析为对应的配置信息
     * 
     * @param element
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    private void parseColumn(Element element) throws Exception {

        String source = element.getAttribute("source");
        String target = element.getAttribute("target");

        LogInfoParser parser = null;
        Integer sqlType = null;
        Class sourceType = null;

        Method method = logInfoMapper.get(source);
        if (method != null) {
            sourceType = method.getReturnType();
        }

        String sqlTypeName = element.getAttribute("sqlType");
        if (StringUtils.isNotBlank(sqlTypeName)) {
            sqlType = sqlTypeMap.get(sqlTypeName.toUpperCase());
            if (sqlType == null) {
                info("unknow sqlType " + sqlTypeName);
            }
        }

        String parserName = element.getAttribute("parser");
        if (StringUtils.isNotBlank(parserName)) {
            parser = (LogInfoParser) ReflectionUtils.instantiateClass(parserName);
        } else {
            parser = defaultParser;
        }

        this.columnElements.add(new ColumnElement(source, sourceType, target, sqlType, parser));
    }

    static class Column {

        // 字段的值获取解析工具
        final ColumnElement ce;

        // 字段位于声明语句中的index
        final int index;

        public Column(ColumnElement columnElement, int index) {
            this.ce = columnElement;
            this.index = index;
        }

        public void setStatementValue(PreparedStatement pstmt, LogInfo logInfo) throws SQLException {
            Object value = ce.parser.parse(ce.source, logInfo);
            if (value == null) {
                pstmt.setNull(index, Types.NULL);
            } else if (ce.sqlType == null) {
                if (value instanceof String) {
                    setString(pstmt, (String) value);
                } else if (value instanceof Message) {
                    setString(pstmt, ((Message) value).getFormattedMessage());
                } else if (value instanceof Throwable) {
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
                } else if (value instanceof Enum<?>) {
                    setString(pstmt, ((Enum<?>) value).name());
                } else {
                    setString(pstmt, String.valueOf(value));
                }
            } else {
                pstmt.setObject(index, value, ce.sqlType);
            }
        }

        private void setString(PreparedStatement pstmt, String value) throws SQLException {
            pstmt.setString(index, value);
        }

        private void setDate(PreparedStatement pstmt, long timeInMillis) throws SQLException {
            pstmt.setTimestamp(index, new Timestamp(timeInMillis));
        }
    }

    private static class ColumnElement {

        // 源字段(logInfo中的字段)
        final String source;
        // 源字段类型
        @SuppressWarnings("unused")
        final Class<?> sourceType;
        // 目标字段(目标表的字段)
        final String target;
        // 表中的字段类型
        final Integer sqlType;

        final LogInfoParser parser;

        public ColumnElement(String source, Class<?> sourceType, String target, Integer sqlType, LogInfoParser parser) {
            this.source = source;
            this.sourceType = sourceType;
            this.target = target;
            this.sqlType = sqlType;
            this.parser = parser;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((source == null) ? 0 : source.hashCode());
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
            if (source == null) {
                if (other.source != null)
                    return false;
            } else if (!source.equals(other.source))
                return false;
            return true;
        }

        @Override
        public String toString() {
            return "ColumnElement {" + source + " -> " + target + "}";
        }

    }

    private static void info(String text) {
        if (level == 1) {
            System.err.println("log4j:" + text);
        }
    }

    private static void debug(String text) {
        if (level == 0) {
            System.out.println("log4j:" + text);
        }
    }
}
