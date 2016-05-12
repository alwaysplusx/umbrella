package com.harmony.umbrella.xml;

import java.util.Iterator;

import org.w3c.dom.Element;

/**
 * element迭代器
 * 
 * @author wuxii@foxmail.com
 */
public class SampleElementIterator implements Iterator<ElementContext> {

    private Element root;
    private ElementContext rootContext;

    public SampleElementIterator(Element element) {
        this.root = element;
        this.rootContext = new ElementContext(element, "", null);
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public ElementContext next() {
        return null;
    }

    public ElementContext getElementContext() {
        return null;
    }

}
