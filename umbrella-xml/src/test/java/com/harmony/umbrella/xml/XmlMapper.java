package com.harmony.umbrella.xml;

import org.w3c.dom.Element;

/**
 * @author wuxii@foxmail.com
 */
public class XmlMapper {

    /**
     * 将xml的节点映射为单个java对象
     * 
     * @param element
     *            xml节点
     * @param mappedType
     *            映射的java类型
     * @return
     */
    public static <T> T mapping(Element element, Class<T> mappedType) {
        return null;
    }

    static Object mapping(Element element) {
        return null;
    }

}
