package com.harmony.umbrella.xml;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;

/**
 * @author wuxii@foxmail.com
 */
public class RssHandler extends DefaultHandler {

    private static final Log log = Logs.getLog(RssHandler.class);
    public static final String CHANNEL = "channel";
    public static final String ITEM = "item";

    private Stack<String> stack = new Stack<String>();

    private String content;

    private Attributes attributes;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        this.attributes = attributes;
        stack.push(qName);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        accessElement(uri, localName, qName, content);
        if (!stack.isEmpty()) {
            stack.pop();
        }
    }

    public void accessElement(String uri, String localName, String qName, String content) {
        log.info("uri={}, localName={}, qName={}, attrs={}, content={}", uri, localName, qName, attributeMap(), content);
        isItemProperty();
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        content = new String(ch, start, length);
    }

    public Map<String, String> attributeMap() {
        if (attributes == null) {
            return Collections.emptyMap();
        }
        Map<String, String> attributeMap = new HashMap<String, String>();
        for (int i = 0; i < attributes.getLength(); i++) {
            attributeMap.put(attributes.getQName(i), attributes.getValue(i));
        }
        return attributeMap;
    }

    protected boolean isChannelProperty() {
        return !isItemProperty();
    }

    protected boolean isItemProperty() {
        for (int i = stack.size(); i > 0; i--) {
            if (ITEM.equalsIgnoreCase(stack.get(i))) {
                return true;
            }
        }
        return false;
    }

}
