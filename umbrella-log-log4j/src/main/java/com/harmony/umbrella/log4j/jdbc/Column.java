package com.harmony.umbrella.log4j.jdbc;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import org.w3c.dom.Element;

import com.harmony.umbrella.log.LogInfo;
import com.harmony.umbrella.log4j.LogInfoParser;
import com.harmony.umbrella.log4j.StaticLogger;
import com.harmony.umbrella.util.ReflectionUtils;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public class Column {

    // 源字段(logInfo中的字段)
    final String source;

    // 源字段类型
    final Class<?> sourceType;

    // 目标字段(目标表的字段)
    final String target;

    // 表中的字段类型
    final Integer sqlType;

    // 字段值获取方式
    private final LogInfoParser parser;

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
        return parser.parse(source, logInfo);
    }

    @Override
    public String toString() {
        return "Column {source:" + source + ", target:" + target + "}";
    }

    static Column getColumn(String source) {
        Method method = logInfoMapper.get(source);
        return new Column(source, method.getReturnType(), source, sqlTypeMap.get(source), null);
    }

    static Column create(Element element) {
        String source = element.getAttribute("source");
        Class<?> sourceType = null;
        String target = element.getAttribute("target");
        Integer sqlType = null;
        LogInfoParser parser = null;

        Method method = logInfoMapper.get(source);
        if (method != null) {
            sourceType = method.getReturnType();
        }

        String sqlTypeName = element.getAttribute("sqlType");
        if (StringUtils.isNotBlank(sqlTypeName)) {
            sqlType = sqlTypeMap.get(sqlTypeName.toUpperCase());
            if (sqlType == null) {
                StaticLogger.warn("unknow sql type " + sqlTypeName);
            }
        }

        String parserName = element.getAttribute("parser");
        if (StringUtils.isNotBlank(parserName)) {
            try {
                parser = (LogInfoParser) ReflectionUtils.instantiateClass(parserName);
            } catch (Exception e) {
                StaticLogger.warn("parser not found " + parserName);
            }
        }

        return new Column(source, sourceType, target, sqlType, parser);
    }

    private static final AtomicLong id = new AtomicLong(System.currentTimeMillis());

    private static final Map<String, Method> logInfoMapper = new HashMap<String, Method>(20);
    private static final Map<String, Integer> sqlTypeMap = new HashMap<String, Integer>();

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
