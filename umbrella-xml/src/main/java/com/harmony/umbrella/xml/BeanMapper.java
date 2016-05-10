package com.harmony.umbrella.xml;

import org.w3c.dom.Element;

import com.harmony.umbrella.core.MemberAccess;
import com.harmony.umbrella.util.StringUtils;

/**
 * TODO 完成实际功能
 * 
 * @author wuxii@foxmail.com
 */
public class BeanMapper {

    public <T> T mapper(Element element, Class<T> targetClass) {
        return null;
    }

    private boolean mapper(Object target, Element element) {
        if (XmlUtil.isLeafElement(element)) {
            MemberAccess.access(target.getClass(), element.getTagName());
            String elemenetValue = getElemenetValue(element);

        } else {

        }
        return true;
    }

    protected String getElemenetValue(Element element) {
        String value = element.getNodeValue();
        return StringUtils.isBlank(value) ? element.getAttribute("value") : value;
    }

    protected String getElementName(Element element) {
        return null;
    }

}
