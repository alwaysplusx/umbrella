package com.harmony.umbrella.xml.convert;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.harmony.umbrella.xml.ValueConverter;

public abstract class AbstractValueConverter<V> implements ValueConverter<V> {

    protected NamedNodeMap attributes;
    protected Class<?> valueType;

    protected abstract void init();

    protected abstract V convertValue(String t);

    @Override
    public void initializ(NamedNodeMap attributes, Class<?> valueType) {
        this.attributes = attributes;
        this.valueType = valueType;
    }

    protected String getAttribute(String name) {
        Node attNode = attributes.getNamedItem(name);
        return attNode != null ? attNode.getNodeValue() : null;
    }

    @Override
    public V convert(String t) {
        if (t == null) {
            return null;
        }
        return convertValue(t);
    }

}