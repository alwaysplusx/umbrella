package com.harmony.umbrella.xml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author wuxii@foxmail.com
 */
public class ElementContext {

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
            List<Element> list = new ArrayList<Element>();
            if (element.hasChildNodes()) {
                NodeList nodeList = element.getChildNodes();
                for (int i = 0, max = nodeList.getLength(); i < max; i++) {
                    Node item = nodeList.item(i);
                    if (XmlUtil.isElement(item)) {
                        list.add((Element) item);
                    }
                }
            }
            this.childElements = list;
        }
        return childElements;
    }

    public String getPath() {
        return path;
    }

}
