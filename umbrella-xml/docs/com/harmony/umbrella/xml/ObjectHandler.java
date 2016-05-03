package com.harmony.umbrella.xml;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.harmony.umbrella.util.Assert;

/**
 * @author wuxii@foxmail.com
 */
public abstract class ObjectHandler<T> extends DefaultHandler implements Iterable<T> {

    protected Class<T> targetType;
    private T[] targets;

    public ObjectHandler(Class<T> targetType) {
        Assert.notNull(targetType, "target type must not be null");
        this.targetType = targetType;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        Map<String, String> attrs = new HashMap<String, String>();
        for (int i = 0; i < attributes.getLength(); i++) {
            attrs.put(attributes.getQName(i), attributes.getValue(i));
        }
        accessElement(qName, attrs);
    }

    public abstract void accessElement(String elementName, Map<String, String> attributes);

    public abstract void accessAttribute(String name, String value);

    public int getLength() {
        return 0;
    }

    public T getAt(int index) {
        return null;
    }

    @Override
    public Iterator<T> iterator() {
        if (targets == null) {
            return Collections.emptyIterator();
        }
        return Arrays.asList(targets).iterator();
    }

}
