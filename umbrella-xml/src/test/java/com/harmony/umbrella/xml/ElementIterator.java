package com.harmony.umbrella.xml;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import org.w3c.dom.Element;

/**
 * element 迭代器
 * 
 * @author wuxii@foxmail.com
 */
public class ElementIterator implements Iterator<ElementIterator> {

    private ElementContext elementContext;
    // 迭代路径的分割符号
    private String pathSeparator;
    // 迭代指针
    private int cursor;
    // 相同名称的tagName已经出现的次数
    private final Map<String, Integer> tagNameShowTimes = new HashMap<String, Integer>();

    public ElementIterator(Element element) {
        this(element, ".");
    }

    public ElementIterator(Element element, String pathSeparator) {
        this(new ElementContext(element, "$", null), pathSeparator);
    }

    private ElementIterator(ElementContext elementContext, String pathSeparator) {
        this.elementContext = elementContext;
        this.pathSeparator = pathSeparator;
    }

    @Override
    public boolean hasNext() {
        return cursor < elementContext.size();
    }

    public Element getElement() {
        return elementContext.getElement();
    }

    public ElementContext getElementContext() {
        return elementContext;
    }

    public String getPathSeparator() {
        return pathSeparator;
    }

    @Override
    public ElementIterator next() {
        if (hasNext()) {
            Element element = elementContext.get(cursor++);

            StringBuilder path = new StringBuilder();
            path.append(elementContext.getPath())//
                    .append(pathSeparator);

            String tagName = element.getTagName();
            path.append(tagName);
            // 如果是复数节点需要添加'[x]'， x为当前出现次数
            if (isPluralTagName(tagName)) {
                path.append("[")//
                        .append(getAndIncrement(tagName))//
                        .append("]");
            }

            ElementContext ec = new ElementContext(element, path.toString(), elementContext);
            return new ElementIterator(ec, pathSeparator);
        }
        throw new NoSuchElementException();
    }

    private boolean isPluralTagName(String tagName) {
        return countTagName(tagName) > 1;
    }

    private int getAndIncrement(String tagName) {
        Integer count = tagNameShowTimes.get(tagName);
        count = (count == null) ? 0 : count;
        tagNameShowTimes.put(tagName, count + 1);
        return count;
    }

    private int countTagName(String tagName) {
        if (tagNameCount == null) {
            Map<String, Integer> countMap = new HashMap<String, Integer>();
            for (Element element : elementContext.getChirdElementList()) {
                Integer count = countMap.get(element.getTagName());
                countMap.put(element.getTagName(), count == null ? 1 : ++count);
            }
            this.tagNameCount = countMap;
        }
        Integer count = tagNameCount.get(tagName);
        return count == null ? 0 : count;
    }

    private Map<String, Integer> tagNameCount;
}
