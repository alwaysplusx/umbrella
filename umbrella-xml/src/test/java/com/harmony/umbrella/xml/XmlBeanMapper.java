package com.harmony.umbrella.xml;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.harmony.umbrella.core.Member;
import com.harmony.umbrella.core.MemberAccess;
import com.harmony.umbrella.util.GenericUtils;
import com.harmony.umbrella.util.ReflectionUtils;
import com.harmony.umbrella.util.StringUtils;

/**
 * 
 * @author wuxii@foxmail.com
 */
public class XmlBeanMapper {

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
        return null;
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
        Member access = MemberAccess.access(target.getClass(), getElementName(element));
        Class<?> valueType = access.getType();
        Object value = null;
        if (ec.hasChirdElements()) {
            if (List.class.isAssignableFrom(valueType)) {
                Field field = access.getField();
                value = mappingList(ei, "bean", GenericUtils.getFieldGeneric(field, 0));
            } else {
                value = mapping(ei, access.getType());
            }
        } else {
            value = convertValue(ec.getElement(), access.getType());
        }
        access.set(target, value);
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

    /**
     * 将没有子节点的element的值转为对应类型的值
     * 
     * @param element
     * @param valueType
     * @return
     */
    @SuppressWarnings("unchecked")
    private <T> T convertValue(Element element, Class<?> valueType) {
        return (T) element.getTextContent();
    }

    protected String getElemenetValue(Element element) {
        String value = element.getNodeValue();
        return StringUtils.isBlank(value) ? element.getAttribute("value") : value;
    }

    protected String getElementName(Element element) {
        String name = element.getAttribute("name");
        return StringUtils.isBlank(name) ? element.getNodeName() : name;
    }

    private <T> T instanceBean(Class<T> clazz) {
        return ReflectionUtils.instantiateClass(clazz);
    }

    public static void main(String[] args) throws Exception {
        Document doc = XmlUtil.getDocument("src/test/resources/bean.xml", true);
        Bean bean = new XmlBeanMapper().mapping(doc.getDocumentElement(), Bean.class);
        System.out.println(bean);
    }

    public static class Bean {
        String name;
        Bean bean;
        ArrayList<Bean> beans;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public ArrayList<Bean> getBeans() {
            return beans;
        }

        public void setBeans(ArrayList<Bean> beans) {
            this.beans = beans;
        }

        public Bean getBean() {
            return bean;
        }

        public void setBean(Bean bean) {
            this.bean = bean;
        }

        @Override
        public String toString() {
            return "Bean [name=" + name + ", bean=" + bean + ", beans=" + beans + "]";
        }

    }

}