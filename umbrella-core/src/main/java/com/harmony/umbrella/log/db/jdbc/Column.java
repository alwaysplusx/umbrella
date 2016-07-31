package com.harmony.umbrella.log.db.jdbc;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.harmony.umbrella.log.LogInfo;
import com.harmony.umbrella.log.db.LogInfoParser;
import com.harmony.umbrella.log.support.StaticLogger;
import com.harmony.umbrella.util.ReflectionUtils;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public class Column {

    // 源字段(logInfo中的字段)
    public final String source;

    // 目标字段(目标表的字段)
    public final String target;

    // 源字段类型
    public final Class<?> sourceType;

    // 表中的字段类型
    public final Integer sqlType;

    // 字段值获取方式
    public final LogInfoParser parser;

    public Column(String source, Class<?> sourceType, String target, Integer sqlType, LogInfoParser parser) {
        this.source = source;
        this.sourceType = sourceType;
        this.target = target;
        this.sqlType = sqlType;
        this.parser = parser;
    }

    public Object getColumnValue(LogInfo logInfo) {
        if (parser != null) {
            return parser.parse(source, logInfo);
        }
        Method method = logInfoMapper.get(source);
        if (method != null) {
            return ReflectionUtils.invokeMethod(method, logInfo);
        } else if ("#uuid".equalsIgnoreCase(source)) {
            return UUID.randomUUID().toString().toUpperCase();
        } else if ("#id".equalsIgnoreCase(source)) {
            return id.getAndIncrement();
        } else if ("#time".equalsIgnoreCase(source)) {
            return new Date();
        }
        throw new IllegalStateException("no log info parser provider");
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((target == null) ? 0 : target.hashCode());
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
        Column other = (Column) obj;
        if (target == null) {
            if (other.target != null)
                return false;
        } else if (!target.equals(other.target))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Column {source:" + source + ", target:" + target + "}";
    }

    private static final AtomicLong id = new AtomicLong(System.currentTimeMillis());

    private static final Map<String, Method> logInfoMapper = new HashMap<String, Method>(20);

    private static final Map<String, Integer> sqlTypeMap = new HashMap<String, Integer>();

    static {
        // 生成LogInfo的信息
        Method[] methods = LogInfo.class.getMethods();
        for (Method method : methods) {
            if (!ReflectionUtils.isObjectMethod(method) && ReflectionUtils.isReadMethod(method)) {
                // in xml configuration name is equal to method sample name.
                // (getModule -> module)
                logInfoMapper.put(getSampleName(method), method);
            }
        }

        Class<?> clazz = java.sql.Types.class;
        for (Field f : clazz.getDeclaredFields()) {
            try {
                sqlTypeMap.put(f.getName().toLowerCase(), (Integer) f.get(clazz));
            } catch (Exception e) {
            }
        }
    }

    static String getSampleName(Method method) {
        String name = method.getName().substring(3);
        return Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }

    static Method getSourceMethod(String name) {
        return logInfoMapper.get(name);
    }

    public static boolean hasSource(String source) {
        return logInfoMapper.containsKey(source);
    }

    public static Set<String> getSources() {
        return logInfoMapper.keySet();
    }

    public static Column createColumn(String source) {
        Method method = getSourceMethod(source);
        return new Column(source, method.getReturnType(), source, sqlTypeMap.get(source), null);
    }

    public static Column[] createColumns(Element element) {
        List<Column> result = new ArrayList<Column>();
        String tagName = element.getTagName();
        if ("columns".equals(tagName) && element.hasChildNodes()) {
            NodeList childNodes = element.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node node = childNodes.item(i);
                if (node instanceof Element) {
                    String childTagName = ((Element) node).getTagName();
                    if ("column".equals(childTagName)) {
                        result.add(Column.createColumn((Element) node));
                        continue;
                    }
                    StaticLogger.warn("unrecognized child element inner columns " + childTagName);
                }
            }
        }
        return result.toArray(new Column[result.size()]);
    }

    public static Column createColumn(Element element) {
        final String source = element.getAttribute("source");
        String target = element.getAttribute("target");

        if (StringUtils.isBlank(source)) {
            return null;
        }

        if (StringUtils.isBlank(target)) {
            target = source;
        }

        Integer sqlType = null;
        Class<?> sourceType = null;
        LogInfoParser parser = null;

        String sqlTypeName = element.getAttribute("sqlType");
        String parserName = element.getAttribute("parser");

        Method method = getSourceMethod(source);
        if (method != null) {
            sourceType = method.getReturnType();
        }

        if (StringUtils.isNotBlank(sqlTypeName)) {
            sqlType = sqlTypeMap.get(sqlTypeName.toLowerCase());
            if (sqlType == null) {
                StaticLogger.warn("unknow sql type " + sqlTypeName);
            }
        }

        if (StringUtils.isNotBlank(parserName)) {
            try {
                parser = (LogInfoParser) ReflectionUtils.instantiateClass(parserName);
            } catch (Exception e) {
                StaticLogger.warn("parser not found " + parserName);
            }
        }

        return new Column(source, sourceType, target, sqlType, parser);
    }

}
