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
    private List<Element> childElements;
    private String path;

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

    public Iterator<Element> getChildElements() {
        return getChaildElementList().iterator();
    }

    private List<Element> getChaildElementList() {
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
            childElements = list;
        }
        return childElements;
    }

    public Element getAt(int index) {
        return getChaildElementList().get(index);
    }

    public boolean hasChildElements() {
        return !getChaildElementList().isEmpty();
    }

    public String getPath() {
        return path;
    }

}
