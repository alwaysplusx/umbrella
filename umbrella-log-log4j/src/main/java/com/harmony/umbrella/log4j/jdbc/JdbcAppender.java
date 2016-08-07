package com.harmony.umbrella.log4j.jdbc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.xml.UnrecognizedElementHandler;
import org.w3c.dom.Element;

import com.harmony.umbrella.log.LogInfo;
import com.harmony.umbrella.log.LoggingException;
import com.harmony.umbrella.log.db.DatabaseManager;
import com.harmony.umbrella.log.db.jdbc.Column;
import com.harmony.umbrella.log.db.jdbc.JdbcDatabaseManager;
import com.harmony.umbrella.log4j.AbstractAppender;
import com.harmony.umbrella.sql.ConnectionSource;
import com.harmony.umbrella.sql.JdbcConnectionSource;
import com.harmony.umbrella.sql.JndiConnectionSource;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public class JdbcAppender extends AbstractAppender implements UnrecognizedElementHandler {

    private String tableName;
    private int bufferSize = 1;
    private boolean upperCase = true;

    private String url;
    private String user;
    private String password;

    private String jndiName;

    private final Set<String> includeSet = new HashSet<String>();

    private boolean autoCommit;

    private List<Column> columns = new ArrayList<Column>();

    private DatabaseManager manager;

    @Override
    protected void append(LogInfo logInfo) {
        manager.write(logInfo);
    }

    protected void init() {
        ConnectionSource connectionSource = null;
        if (jndiName != null) {
            connectionSource = new JndiConnectionSource(jndiName);
        } else if (url != null && user != null && password != null) {
            connectionSource = new JdbcConnectionSource(url, user, password);
        }
        if (connectionSource == null) {
            throw new LoggingException("connection sources not configuration");
        }

        for (String source : includeSet) {
            Column c = Column.createColumn(source);
            if (Column.hasSource(source) && !columns.contains(c)) {
                columns.add(c);
            }
        }

        this.manager = JdbcDatabaseManager.createManager(bufferSize, //
                connectionSource, //
                tableName, //
                upperCase, //
                autoCommit, //
                columns.toArray(new Column[columns.size()]));

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

    public void setAutoCommit(boolean autoCommit) {
        this.autoCommit = autoCommit;
    }

    public void setInclude(String include) {
        if ("all".equalsIgnoreCase(include)) {
            this.includeSet.addAll(Column.getSources());
        } else {
            Collections.addAll(this.includeSet, StringUtils.tokenizeToStringArray(include, ","));
        }
    }

    /**
     * 解析配置appender的自定义tag <code>columns</code>
     * <p/>
     * <p/>
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
        Column[] columns = Column.createColumns(element);
        return columns == null || columns.length == 0;
    }

}
