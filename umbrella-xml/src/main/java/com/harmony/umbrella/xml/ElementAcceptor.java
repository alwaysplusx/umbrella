package com.harmony.umbrella.xml;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.harmony.umbrella.util.XmlUtils;

/**
 * @author wuxii@foxmail.com
 */
public abstract class ElementAcceptor implements NodeAcceptor {

    @Override
    public final boolean accept(String path, Node node) {
        return XmlUtils.isElement(node) ? acceptElement(path, (Element) node) : true;
    }

    public abstract boolean acceptElement(String path, Element element);

}
