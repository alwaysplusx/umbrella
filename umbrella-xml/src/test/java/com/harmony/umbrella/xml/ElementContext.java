package com.harmony.umbrella.xml;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Element;

/**
 * 迭代element生成的element context
 * 
 * @author wuxii@foxmail.com
 */
public class ElementContext implements Iterable<Element> {

    private Element element;
    private ElementContext parent;
    private String path;
    private List<Element> childElements;

    public ElementContext(Element element, String path, ElementContext parent) {
        this.element = element;
        this.parent = parent;
        this.path = path;
    }

    public Element getElement() {
        return element;
    }

    public ElementContext getParent() {
        return parent;
    }

    public Iterator<Element> getChirdElements() {
        return getChirdElementList().iterator();
    }

    public boolean hasChirdElements() {
        return !getChirdElementList().isEmpty();
    }

    public String getElementName() {
        return element.getTagName();
    }

    public String getElementValue() {
        return element.getTextContent();
    }

    public Element get(int index) {
        return getChirdElementList().get(index);
    }

    public int size() {
        return getChirdElementList().size();
    }

    List<Element> getChirdElementList() {
        if (childElements == null) {
            this.childElements = Collections.unmodifiableList(XmlUtil.getChildElementList(element));
        }
        return childElements;
    }

    public String getPath() {
        return path;
    }

    @Override
    public Iterator<Element> iterator() {
        return getChirdElementList().iterator();
    }

}
