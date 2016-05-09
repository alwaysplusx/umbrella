
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
import com.harmony.umbrella.util.ReflectionUtils;

/**
 * @author wuxii@foxmail.com
 */
public class JdbcAppender extends AppenderSkeleton implements UnrecognizedElementHandler {

    private static final Map<String, Method> logInfoMapper = new HashMap<String, Method>(20);

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
    private final List<Column> columns = new ArrayList<Column>();

    private DatabaseManager<LoggingEvent> manager;

    private final Set<ColumnElement> columnElements = new HashSet<ColumnElement>();

    static {
        // 生成LogInfo的信息
        Method[] methods = LogInfo.class.getMethods();
        for (Method method : methods) {
            if (!ReflectionUtils.isObjectMethod(method) && ReflectionUtils.isReadMethod(method)) {
                // in xml configuration name is equal to method sample name. (getModule -> module)
                logInfoMapper.put(getSampleName(method), method);
            }
        }
    }

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
        List<ColumnElement> sortedColumnElements = sortColumnElement(columnElements);

        // build sql, and column
        StringBuilder columnPart = new StringBuilder();
        StringBuilder valuePart = new StringBuilder();

        Iterator<ColumnElement> it = sortedColumnElements.iterator();

        int index = 1;
        while (it.hasNext()) {
            ColumnElement ce = it.next();

            if (logInfoMapper.containsKey(ce.source)) {

                columnPart.append(upperCase ? ce.target.toUpperCase() : ce.target);
                valuePart.append("?");

                this.columns.add(new Column(logInfoMapper.get(ce.source), ce, index++));

                if (it.hasNext()) {
                    columnPart.append(", ");
                    valuePart.append(", ");
                }

            }
        }

        this.sqlStatement = "INSERT INTO " + tableName + "(" + columnPart + ") VALUES (" + valuePart + ")";

        debug("sql statement " + this.sqlStatement);
    }

    /**
     * 排序
     */
    private List<ColumnElement> sortColumnElement(Set<ColumnElement> ce) {
        ArrayList<ColumnElement> list = new ArrayList<ColumnElement>(ce);
        Collections.sort(list, new Comparator<ColumnElement>() {
            @Override
            public int compare(ColumnElement o1, ColumnElement o2) {
                return o1.source.compareTo(o2.source);
            }
        });
        return list;
    }

    /**
     * 自动映射数据库字段与logInfo的关系
     */
    private void autoMatchField() {
        Set<String> fields = logInfoMapper.keySet();
        for (String field : fields) {
            if (isAutoMatchField(field)) {
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
        }
        return false;
    }

    private void parseColumn(Element element) {

        String source = element.getAttribute("source");
        String target = element.getAttribute("target");
        String dateType = element.getAttribute("dateType");

        if (dateType == null || "".equals(dateType)) {
            dateType = ColumnElement.DATE_TYPE_NONE;
        }

        boolean isClob = Boolean.valueOf(element.getAttribute("isClob"));

        columnElements.add(new ColumnElement(source, target, dateType, isClob));
    }

    private static String getSampleName(Method method) {
        String name = method.getName().substring(3);
        return Character.toLowerCase(name.charAt(0)) + name.substring(1);
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

        final String source;
        final String target;
        final boolean isClob;
        final String dateType;

        public ColumnElement(String source, String target, String dateType, boolean isClob) {
            this.source = source;
            this.target = target;
            this.isClob = isClob;
            this.dateType = dateType;
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
            return "ColumnElement[" + source + "->" + target + "]";
        }
    }

}
