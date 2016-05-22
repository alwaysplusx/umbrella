package com.harmony.umbrella.log4j.jdbc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.xml.UnrecognizedElementHandler;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.harmony.umbrella.log.LogInfo;
import com.harmony.umbrella.log.LoggingException;
import com.harmony.umbrella.log.jdbc.ConnectionSource;
import com.harmony.umbrella.log.jdbc.DatabaseManager;
import com.harmony.umbrella.log.jdbc.JdbcConnectionSource;
import com.harmony.umbrella.log.jdbc.JndiConnectionSource;
import com.harmony.umbrella.log4j.StaticLogger;

/**
 * @author wuxii@foxmail.com
 */
public class JdbcAppender extends AppenderSkeleton implements UnrecognizedElementHandler {

    private String tableName;
    private int bufferSize = 1;
    private boolean upperCase = true;

    private String url;
    private String user;
    private String password;

    private String jndiName;

    private final Set<String> includeSet = new HashSet<String>();

    // 根据配置生成的信息
    private boolean initialize;

    // 最终解析后的columns
    private final Map<String, Column> columnMap = new HashMap<String, Column>();

    private ConnectionSource connectionSource;

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

                    initConnectionSource();

                    // 数据存储管理初始化
                    initDatabaseManager();

                    this.initialize = true;
                }
            }
        }
    }

    private void initConnectionSource() {
        if (jndiName != null) {
            this.connectionSource = new JndiConnectionSource(jndiName);
        } else if (url != null && user != null && password != null) {
            this.connectionSource = new JdbcConnectionSource(url, user, password);
        }
    }

    private void initDatabaseManager() {
        if (connectionSource == null) {
            throw new LoggingException("connection sources not configuration");
        }

        for (String source : includeSet) {
            if (!columnMap.containsKey(source) && Column.hasSource(source)) {
                columnMap.put(source, Column.getColumn(source));
            }
        }

        List<Column> columns = new ArrayList<Column>(columnMap.values());
        Collections.sort(columns);
        // build sql, and column
        StringBuilder columnPart = new StringBuilder();
        StringBuilder valuePart = new StringBuilder();

        for (int i = 0, max = columns.size(); i < max; i++) {
            Column column = columns.get(i);
            column.index = i + 1;
            // 存在于logInfo中的属性
            columnPart.append(upperCase ? column.target.toUpperCase() : column.target);
            valuePart.append("?");
            if (i + 1 < max) {
                columnPart.append(", ");
                valuePart.append(", ");
            }
        }

        String sqlStatement = "INSERT INTO " + (upperCase ? tableName.toUpperCase() : tableName) + " (" + columnPart + ") VALUES (" + valuePart + ")";

        StaticLogger.info("sql statement " + sqlStatement);

        this.manager = new JdbcDatabaseManager(bufferSize, connectionSource, sqlStatement, columns);
        this.manager.startup();
    }

    @Override
    public boolean requiresLayout() {
        return false;
    }

    @Override
    public void close() {
    }

    public boolean isInclude(String name) {
        return includeSet.contains(name);
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

    public void setInclude(String include) {
        if ("all".equalsIgnoreCase(include)) {
            this.includeSet.addAll(Column.getSources());
        } else {
            this.includeSet.addAll(splitAndTrim(include));
        }
    }

    protected Set<String> splitAndTrim(String text) {
        Set<String> result = new HashSet<String>();
        for (String t : text.split(",")) {
            result.add(t.trim());
        }
        return result;
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
     * @see org.apache.log4j.xml.UnrecognizedElementHandler#parseUnrecognizedElement(org.w3c.dom.Element, java.util.Properties)
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
                        Column column = Column.create((Element) node);
                        columnMap.put(column.source.toLowerCase(), column);
                        continue;
                    }
                    StaticLogger.warn("unrecognized child element inner columns " + childTagName);
                }
            }
            return true;
        }
        return false;
    }
}
