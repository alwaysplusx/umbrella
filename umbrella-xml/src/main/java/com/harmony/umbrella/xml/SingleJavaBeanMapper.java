package com.harmony.umbrella.xml;

import java.lang.reflect.Field;

import org.w3c.dom.Element;

import com.harmony.umbrella.util.ClassUtils;
import com.harmony.umbrella.util.Converter;
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

    public SingleJavaBeanMapper(Class<T> mapperType) {
        super(mapperType);
    }

    @Override
    protected void setTargetValue(String path, Element element) {
        // 截取path到指定targetField的上一层
        Object target = getTarget(path.substring(0, path.length() - element.getTagName().length()));
        Field targetField = getTargetField(target.getClass(), element);
        Class<?> targetFieldType = targetField.getType();
        Object targetFieldValue = null;
        if (XmlUtil.isLeafElement(element)) {
            targetFieldValue = (targetFieldType == String.class) ? element.getTextContent() : convertFieldValue(targetFieldType, element);
        } else {
            targetFieldValue = newFieldValue(targetFieldType, element);
        }
        ReflectionUtils.setFieldValue(element.getTagName(), target, targetFieldValue);
    }

    private Field getTargetField(Class<?> targetClass, Element element) {
        String fieldName = element.getAttribute("field");
        return getTargetField(targetClass, StringUtils.isBlank(fieldName) ? element.getTagName() : fieldName);
    }

    private Object newFieldValue(Class<?> expectType, Element element) {
        String typeName = element.getAttribute("type");
        if (StringUtils.isNotBlank(typeName)) {
            // 节点定制的类型
            try {
                Class<?> actualType = ClassUtils.forName(typeName);
                if (!expectType.isAssignableFrom(actualType)) {
                    throw new MappingException("mismatch element type, expect " + expectType.getName() + " but actual " + actualType.getName());
                }
                return ReflectionUtils.instantiateClass(actualType);
            } catch (ClassNotFoundException e) {
                throw new MappingException(e);
            }
        } else {
            // 没有定制类型, 直接通过目标类型创建
            return ReflectionUtils.instantiateClass(expectType);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Object convertFieldValue(Class<?> expectType, Element element) {
        Converter converter = getCustomerConverter(element.getAttribute("convert"), expectType);
        return converter.convert(element.getTextContent());
    }

    /**
     * 获取target中字段名称为tagName的类型
     * 
     * @param targetClass
     *            目标对象
     * @param tagName
     *            字段名称
     * @return 字段对应的类型
     */
    protected Field getTargetField(Class<?> targetClass, String fieldName) {
        try {
            return ReflectionUtils.findField(targetClass, fieldName);
        } catch (Exception e) {
            throw new MappingException(e);
        }
    }

    @SuppressWarnings("rawtypes")
    protected Converter getCustomerConverter(String converterName, Class<?> fieldType) {
        Converter convert = null;
        if (StringUtils.isNotBlank(converterName)) {
            try {
                convert = (Converter) ReflectionUtils.instantiateClass(ClassUtils.forName(converterName));
            } catch (ClassNotFoundException e) {
                throw new MappingException(e);
            }
        } else {
            convert = getConvert(fieldType);
        }
        if (convert == null) {
            throw new MappingException("cannot found converter for " + fieldType);
        }
        return convert;
    }

}