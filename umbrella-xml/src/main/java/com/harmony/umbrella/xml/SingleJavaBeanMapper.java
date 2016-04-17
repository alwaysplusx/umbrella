package com.harmony.umbrella.xml;

import org.w3c.dom.Element;

/**
 * 将单个xml element映射为java对象
 * <p>
 * FIXME 暂时只支持简单的对象映射, 对象中不能包含集合类型
 * 
 * @author wuxii@foxmail.com
 */
public final class SingleJavaBeanMapper<T> extends XmlBeanMapper<T> {

    private static final String TAG_LIST = "list";
    private static final String TAG_MAP = "map";

    public SingleJavaBeanMapper(Class<T> mappedType) {
        super(mappedType);
    }

    @Override
    protected boolean setTargetFieldValue(T target, String fieldPath, Element element) {
        getType(target.getClass(), fieldPath);
        // System.out.println(getType(target.getClass(), fieldPath));
        return true;
    }

    public boolean isListElement(Element element) {
        return TAG_LIST.equals(element.getTagName()) || TAG_LIST.equals(element.getAttribute("type"));
    }

    public boolean isMapElement(Element element) {
        return TAG_MAP.equals(element.getTagName()) || TAG_MAP.equals(element.getAttribute("type"));
    }

    public Class<?> getType(Class<?> target, String path) {
        System.out.println(path.subSequence(rootPath.length() + 1, path.length()));
        return null;
    }

}