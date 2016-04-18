package com.harmony.umbrella.xml;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.w3c.dom.Element;

import com.harmony.umbrella.util.ReflectionUtils;
import com.harmony.umbrella.util.StringUtils;

/**
 * 将单个xml element映射为java对象
 * <p>
 * FIXME 暂时只支持简单的对象映射, 对象中不能包含集合类型
 * 
 * @author wuxii@foxmail.com
 */
public final class SingleJavaBeanMapper<T> extends XmlBeanMapper<T> {

    private static final String TAG_LIST = "list";
    private static final String TAG_MAP = "map";

    private static Set<Class<?>> converterTypes = new HashSet<Class<?>>();

    static {

        converterTypes.add(boolean.class);
        converterTypes.add(byte.class);
        converterTypes.add(char.class);
        converterTypes.add(double.class);
        converterTypes.add(float.class);
        converterTypes.add(int.class);
        converterTypes.add(long.class);
        converterTypes.add(short.class);

        converterTypes.add(Boolean.class);
        converterTypes.add(Byte.class);
        converterTypes.add(Character.class);
        converterTypes.add(Float.class);
        converterTypes.add(Integer.class);
        converterTypes.add(Long.class);
        converterTypes.add(Short.class);

        converterTypes.add(String.class);
        converterTypes.add(Date.class);
        converterTypes.add(Calendar.class);

    }

    public SingleJavaBeanMapper(Class<T> mappedType) {
        super(mappedType);
    }

    @Override
    protected boolean setTargetFieldValue(T target, String fieldPath, Element element) {
        // 指向的字段名称
        String targetFieldName = element.getTagName();
        // 指向field的parent object路径
        String realTargetPath = fieldPath.substring(0, fieldPath.lastIndexOf(".") != -1 ? fieldPath.lastIndexOf(".") : 0);
        Object realTarget = getPathValue(target, realTargetPath);
        Field targetField = ReflectionUtils.findField(realTarget.getClass(), targetFieldName);

        if (XmlUtil.isLeafElement(element)) {

        } else {
            Object targetFieldValue = XmlMapper.mapping(element);
        }

        return true;
    }

    public Object getPathValue(Object target, String path) {
        if (StringUtils.isBlank(path)) {
            return target;
        }
        StringTokenizer st = new StringTokenizer(path, ".");
        while (st.hasMoreTokens()) {
            final String token = st.nextToken();
            if (token.indexOf("[") > 0) {
                String name = token.substring(0, token.indexOf('['));
                int index = Integer.valueOf(token.substring(token.indexOf("["), token.indexOf("]")));
                target = getValueAt(ReflectionUtils.getFieldValue(name, target), index);
            } else {
                target = ReflectionUtils.getFieldValue(token, target);
            }
        }
        return target;
    }

    @SuppressWarnings("rawtypes")
    private Object getValueAt(Object value, int index) {
        Class<?> targetClass = value.getClass();
        if (isListType(targetClass)) {
            return ((List) value).get(index);
        } else if (targetClass.isArray()) {
            return getValueInArray(index, value);
        }
        throw new IllegalArgumentException(value + " not array type");
    }

    private Object getValueInArray(int index, Object value) {
        Object result = null;
        if (value instanceof Object[]) {
            result = ((Object[]) value)[index];
        } else if (value instanceof int[]) {
            result = ((int[]) value)[index];
        } else if (value instanceof long[]) {
            result = ((long[]) value)[index];
        } else if (value instanceof char[]) {
            result = ((char[]) value)[index];
        } else if (value instanceof short[]) {
            result = ((short[]) value)[index];
        } else if (value instanceof float[]) {
            result = ((float[]) value)[index];
        } else if (value instanceof double[]) {
            result = ((double[]) value)[index];
        } else if (value instanceof boolean[]) {
            result = ((boolean[]) value)[index];
        } else {
            throw new IllegalArgumentException("unsupported array type " + value.getClass().getName());
        }
        return result;
    }

    public boolean isListElement(Element element) {
        return TAG_LIST.equals(element.getTagName()) || TAG_LIST.equals(element.getAttribute("type"));
    }

    public boolean isMapElement(Element element) {
        return TAG_MAP.equals(element.getTagName()) || TAG_MAP.equals(element.getAttribute("type"));
    }

    protected boolean isListType(Class<?> type) {
        return List.class.isAssignableFrom(type);
    }

    protected boolean isMapType(Class<?> type) {
        return Map.class.isAssignableFrom(type);
    }

    public Class<?> getType(Class<?> target, String path) {
        System.out.println(path.subSequence(rootPath.length() + 1, path.length()));
        return null;
    }

}