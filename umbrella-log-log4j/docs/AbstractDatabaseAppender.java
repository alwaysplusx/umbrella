package com.harmony.umbrella.log;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.xml.UnrecognizedElementHandler;
import org.w3c.dom.Element;

/**
 * @author wuxii@foxmail.com
 */
public class AbstractDatabaseAppender extends AppenderSkeleton implements UnrecognizedElementHandler {

    protected static final String COLUMN_TAG = "column";

    private static final Map<String, Method> columnMap = new HashMap<String, Method>(20);

    static {
        Method[] methods = LogInfo.class.getMethods();
        for (Method method : methods) {
            if (method.getDeclaringClass() != Object.class && method.getName().startsWith("get")) {
                columnMap.put(getSampleName(method), method);
            }
        }
    }

    private Set<Column> columns = new HashSet<Column>();

    @Override
    public boolean parseUnrecognizedElement(Element element, Properties props) throws Exception {
        String tagName = element.getTagName();

        if (tagName.equals(COLUMN_TAG)) {
            String name = element.getAttribute("name");
            if (columnMap.containsKey(name)) {

                boolean isClob = Boolean.valueOf(element.getAttribute("isClob"));
                boolean isTimestamp = Boolean.valueOf(element.getAttribute("isClob"));

                columns.add(new Column(name, columnMap.get(name), isTimestamp, isClob));

            }
            return true;
        }

        return false;
    }

    @Override
    protected void append(LoggingEvent event) {
    }

    @Override
    public void close() {
    }

    @Override
    public boolean requiresLayout() {
        return true;
    }

    protected final static String getSampleName(Method method) {
        String name = method.getName().substring(3);
        return Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }

}
