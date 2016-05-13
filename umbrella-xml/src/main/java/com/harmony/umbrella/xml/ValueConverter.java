package com.harmony.umbrella.xml;

import org.w3c.dom.NamedNodeMap;

import com.harmony.umbrella.util.Converter;

/**
 * @author wuxii@foxmail.com
 */
public interface ValueConverter<V> extends Converter<String, V> {

    void initializ(NamedNodeMap attributes, Class<?> valueType);

    V convert(String t);
}
