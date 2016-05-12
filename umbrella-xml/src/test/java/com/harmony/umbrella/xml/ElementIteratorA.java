package com.harmony.umbrella.xml;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import org.w3c.dom.Element;

/**
 * @author wuxii@foxmail.com
 */
public class ElementIteratorA implements Iterator<ElementIteratorA> {

    private ElementContext elementContext;
    private String separator;
    // 迭代指针
    private int cursor;

    private final Map<String, Integer> tagNameShowTimes = new HashMap<String, Integer>();

    public ElementIteratorA(Element element) {
        this(element, ".");
    }

    public ElementIteratorA(Element element, String separator) {
        this.elementContext = new ElementContext(element, "$", null);
        this.separator = separator;
    }

    public ElementIteratorA(ElementContext elementContext) {
        this(elementContext, ".");
    }

    public ElementIteratorA(ElementContext elementContext, String separator) {
        this.elementContext = elementContext;
        this.separator = separator;
    }

    @Override
    public boolean hasNext() {
        return cursor < elementContext.size();
    }

    public ElementContext getElementContext() {
        return elementContext;
    }

    @Override
    public ElementIteratorA next() {
        if (hasNext()) {
            Element element = elementContext.get(cursor++);

            StringBuilder path = new StringBuilder();
            path.append(elementContext.getPath())//
                    .append(separator);

            String tagName = element.getTagName();
            path.append(tagName);
            if (isPluralTagName(tagName)) {
                path.append("[")//
                        .append(getAndIncrement(tagName))//
                        .append("]");
            }

            ElementContext ec = new ElementContext(element, path.toString(), elementContext);
            return new ElementIteratorA(ec, separator);
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
