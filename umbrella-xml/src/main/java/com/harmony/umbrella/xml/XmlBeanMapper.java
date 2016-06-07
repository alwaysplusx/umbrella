package com.harmony.umbrella.xml;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import com.harmony.umbrella.access.Member;
import com.harmony.umbrella.access.MemberAccess;
import com.harmony.umbrella.util.ClassUtils.ClassFilterFeature;
import com.harmony.umbrella.util.GenericUtils;
import com.harmony.umbrella.util.ReflectionUtils;
import com.harmony.umbrella.util.StringUtils;

/**
 * 
 * @author wuxii@foxmail.com
 */
public class XmlBeanMapper {

    @SuppressWarnings("rawtypes")
    private final Map<Class, List> valueConveterMap = new HashMap<Class, List>();

    public XmlBeanMapper() {
    }

    @SuppressWarnings("rawtypes")
    public XmlBeanMapper(Class<? extends ValueConverter>... vcs) {
        this(Arrays.asList(vcs));
    }

    @SuppressWarnings("rawtypes")
    public XmlBeanMapper(List<Class<? extends ValueConverter>> vcs) {
        for (Class<? extends ValueConverter> clazz : vcs) {
            this.addValueConverter(clazz);
        }
    }

    /**
     * 输入element表示target. 由根节点进入， 将element节点下的内容与targetClass中的字段进行匹配
     * 
     * @param element
     * @param targetClass
     * @return
     */
    public <T> T mapping(Element element, Class<T> targetClass) {
        return mapping(new ElementIterator(element), targetClass);
    }

    public <T> List<T> mappingList(Element element, String itemTagName, Class<T> itemType) {
        return mappingList(new ElementIterator(element), itemTagName, itemType);
    }

    /**
     * 迭代器中的各个节点与targetClass中的字段一一对应
     * 
     * @param ei
     *            element迭代器
     * @param targetClass
     *            目标类
     * @return
     */
    private <T> T mapping(ElementIterator ei, Class<T> targetClass) {
        final T target = instanceBean(targetClass);
        while (ei.hasNext()) {
            mappingMember(ei.next(), target);
        }
        return target;
    }

    /**
     * 对应的element为target中的字段， 将当前element与target中对应的字段进行匹配
     * 
     * @param ei
     *            element迭代工具
     * @param target
     *            设置目标
     */
    private void mappingMember(ElementIterator ei, Object target) {
        ElementContext ec = ei.getElementContext();
        Element element = ec.getElement();

        Member member = getTargetMember(target.getClass(), element);
        Class<?> memberType = member.getType();
        Object memberValue = null;

        if (ec.hasChirdElements()) {
            // TODO use element converter
            if (List.class.isAssignableFrom(memberType)) {
                Field field = member.getField();
                // list对应的泛型类型
                Class<?> type = GenericUtils.getFieldGeneric(field, 0);
                memberValue = mappingList(ei, "item", type);
            } else {
                memberValue = mapping(ei, member.getType());
            }
        } else {
            memberValue = convertValue(ec.getElement(), member.getType());
        }

        member.set(target, memberValue);
    }

    private <T> List<T> mappingList(ElementIterator ei, String itemName, Class<T> itemType) {
        List<T> result = new ArrayList<T>();
        while (ei.hasNext()) {
            ElementIterator next = ei.next();
            String elementName = next.getElementContext().getElementName();
            if (itemName.equals(elementName)) {
                result.add(mapping(next, itemType));
            }
        }
        return result;
    }

    protected Member getTargetMember(Class<?> clazz, Element element) {
        String name = element.getAttribute("name");
        name = StringUtils.isBlank(name) ? element.getNodeName() : name;
        return MemberAccess.access(clazz, name);
    }

    /**
     * 将没有子节点的element的值转为对应类型的值
     * 
     * @param element
     * @param valueType
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected <T> T convertValue(Element element, Class<?> valueType) {
        ValueConverter converter = getCustomeValueConverter(element, valueType);
        if (converter == null) {
            throw new MappingException("cannot found converter for " + valueType);
        }
        return (T) element.getTextContent();
    }

    @SuppressWarnings({ "rawtypes" })
    protected ValueConverter getCustomeValueConverter(Element element, Class<?> valueType) {
        ValueConverter result = null;
        String convertName = element.getAttribute("convert");
        if (StringUtils.isNotBlank(convertName)) {
            try {
                result = (ValueConverter) ReflectionUtils.instantiateClass(convertName);
            } catch (Exception e) {
                throw new IllegalArgumentException(convertName + " cannot instance", e);
            }
        } else {
            result = getValueConverter(valueType);
        }
        return result;
    }

    @SuppressWarnings("rawtypes")
    public void addValueConverter(Class<? extends ValueConverter> valueConverterClass) {
        if (!ClassFilterFeature.NEWABLE.accept(valueConverterClass)) {
            throw new IllegalArgumentException(valueConverterClass + " not newable valueConverter");
        }
        Class<?> type = GenericUtils.getTargetGeneric(valueConverterClass, ValueConverter.class, 0);
        getValueConverterList(type).add(valueConverterClass);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private ValueConverter getValueConverter(Class<?> valueType) {
        ValueConverter result = null;
        List<Class<? extends ValueConverter>> list = getValueConverterList(valueType);
        for (Class clazz : list) {
            try {
                result = ReflectionUtils.<ValueConverter> instantiateClass(clazz);
                if (result != null) {
                    break;
                }
            } catch (Exception e) {
            }
        }
        return result;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private List<Class<? extends ValueConverter>> getValueConverterList(Class<?> valueType) {
        List result = valueConveterMap.get(valueType);
        // 直接匹配
        if (result == null) {
            valueConveterMap.put(valueType, result = new ArrayList());
        }
        // 直接匹配
        if (result.isEmpty()) {
            result = new ArrayList();
            // 找寻子类的access， 子类的access不保存与type的关联关系
            for (Class clazz : valueConveterMap.keySet()) {
                if (clazz.isAssignableFrom(valueType)) {
                    result.addAll(valueConveterMap.get(clazz));
                }
            }
        }

        Collections.sort(result, new Comparator<Class>() {

            @Override
            public int compare(Class o1, Class o2) {
                return (!o1.isAssignableFrom(o2) && o1.equals(o2)) ? 0 : ((o1.isAssignableFrom(o2)) ? 1 : -1);
            }
        });

        return result;
    }

    private <T> T instanceBean(Class<T> clazz) {
        return ReflectionUtils.instantiateClass(clazz);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected List getValueConverterClasses() {
        List result = new ArrayList();
        for (List vcs : valueConveterMap.values()) {
            result.add(vcs);
        }
        return result;
    }
}