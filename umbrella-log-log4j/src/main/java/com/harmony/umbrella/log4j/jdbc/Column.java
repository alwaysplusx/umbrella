package com.harmony.umbrella.log4j.jdbc;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Element;

import com.harmony.umbrella.log.LogInfo;
import com.harmony.umbrella.log4j.StaticLogger;
import com.harmony.umbrella.log4j.parser.LogInfoParser;
import com.harmony.umbrella.log4j.parser.NamedParser;
import com.harmony.umbrella.util.ReflectionUtils;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public class Column implements Comparable<Column> {

    // 源字段(logInfo中的字段)
    String source;

    // 源字段类型
    Class<?> sourceType;

    // 目标字段(目标表的字段)
    String target;

    // 表中的字段类型
    Integer sqlType;

    // 字段值获取方式
    LogInfoParser parser;

    int index;

    private Column() {
    }

    public void setStatementValue(PreparedStatement statement, LogInfo logInfo) throws SQLException {
        LogStatementUtils.setValue(statement, index, sqlType, parser.parse(source, logInfo));
    }

    @Override
    public int compareTo(Column o) {
        return source.compareTo(o.source);
    }

    @Override
    public String toString() {
        return "Column {source:" + source + ", target:" + target + "}";
    }

    static Column getColumn(String source) {
        Method method = logInfoMapper.get(source);
        Column column = new Column();
        column.source = source;
        column.target = source;
        column.sourceType = method.getReturnType();
        column.parser = defaultParser;
        column.sqlType = sqlTypeMap.get(column.sourceType);
        return column;
    }

    static Column create(Element element) {
        Column column = new Column();
        column.source = element.getAttribute("source");
        column.target = element.getAttribute("target");

        Method method = logInfoMapper.get(column.source);
        if (method != null) {
            column.sourceType = method.getReturnType();
        }

        String sqlTypeName = element.getAttribute("sqlType");
        if (StringUtils.isNotBlank(sqlTypeName)) {
            column.sqlType = sqlTypeMap.get(sqlTypeName.toUpperCase());
            StaticLogger.warn("log4j: unknow sql type " + sqlTypeName);
        }

        String parserName = element.getAttribute("parser");
        if (StringUtils.isNotBlank(parserName)) {
            try {
                column.parser = (LogInfoParser) ReflectionUtils.instantiateClass(parserName);
            } catch (Exception e) {
            }
        }
        if (column.parser == null) {
            column.parser = defaultParser;
        }
        return column;
    }

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
                sqlTypeMap.put(f.getName().toLowerCase(), (Integer) f.get(clazz));
            } catch (Exception e) {
            }
        }
    }

    private static String getSampleName(Method method) {
        String name = method.getName().substring(3);
        return Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }

    public static Method getSourceMethod(String name) {
        return logInfoMapper.get(name);
    }

    public static Set<String> getSources() {
        return logInfoMapper.keySet();
    }

    public static boolean hasSource(String source) {
        return logInfoMapper.containsKey(source);
    }

}
