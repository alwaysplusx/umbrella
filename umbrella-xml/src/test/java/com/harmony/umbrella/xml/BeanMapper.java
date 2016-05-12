package com.harmony.umbrella.xml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Element;

import com.harmony.umbrella.core.Member;
import com.harmony.umbrella.core.MemberAccess;
import com.harmony.umbrella.util.Converter;
import com.harmony.umbrella.util.ReflectionUtils;
import com.harmony.umbrella.util.StringUtils;

/**
 * TODO 完成实际功能
 * 
 * @author wuxii@foxmail.com
 */
public class BeanMapper {

    private List<Converter<String, ?>> converters = new ArrayList<Converter<String, ?>>();

    public <T> T mapping(Element root, Class<T> targetClass) {
        ElementIterator eit = XmlUtil.iterator(root);
        if (eit.isLeaf()) {
            Converter<String, ?> converter = getElementConverter(root);
            if (converter == null) {
                converter = getConvertByType(targetClass);
            }
        } else {

        }
        T result = ReflectionUtils.instantiateClass(targetClass);
        Iterator<Element> childElements = eit.iterator();
        mapper(root, result);
        return result;
    }

    private void mapper(Element element, Object target) {

        mapper(XmlUtil.iterator(element), target);
    }

    private void mapper(ElementIterator eit, Object target) {
        Element element = eit.getCurrentElement();
        String name = getElementName(element);
        Member member = MemberAccess.access(target.getClass(), name);
        if (eit.isLeaf()) {
            // 字段值匹配
            if (member.isWriteable()) {
                String elemenetValue = getElemenetValue(element);
                Class<?> memberType = member.getType();
                Converter<String, ?> converter = getElementConverter(element);
                if (converter == null) {
                    converter = getConvertByType(member.getType());
                }
                Object value = converter.convert(elemenetValue);
                if (value != null && !memberType.isInstance(value)) {
                    throw new MappingException(eit.getPath() + " convert mismatch, expect " + memberType + " but actual " + value.getClass());
                }
                member.set(target, value);
            }
        } else {
            // 复杂对象匹配
            Object obj = member.get(target);
            if (obj == null) {
                obj = ReflectionUtils.instantiateClass(member.getType());
            }
            Iterator<Element> childElements = eit.iterator();
            while (childElements.hasNext()) {
                mapper(childElements.next(), obj);
            }

        }
    }

    protected Converter<String, ?> getConvertByType(Class<?> requireType) {
        return new StringToStringConverter();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected Converter<String, ?> getElementConverter(Element element) {
        String convertClassName = element.getAttribute("convert");
        try {
            return (Converter) ReflectionUtils.instantiateClass(convertClassName);
        } catch (Exception e) {
            return null;
        }
    }

    protected String getElemenetValue(Element element) {
        String value = element.getNodeValue();
        return StringUtils.isBlank(value) ? element.getAttribute("value") : value;
    }

    protected String getElementName(Element element) {
        String name = element.getAttribute("name");
        return StringUtils.isBlank(name) ? element.getNodeName() : name;
    }

    public void addConverter(Converter<String, ?> converter) {
        converters.add(converter);
    }
}