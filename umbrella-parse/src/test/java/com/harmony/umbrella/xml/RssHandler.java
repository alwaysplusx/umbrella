/*
 * Copyright 2012-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.harmony.umbrella.xml;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author wuxii@foxmail.com
 */
public class RssHandler extends DefaultHandler {

    private static final Logger log = LoggerFactory.getLogger(RssHandler.class);
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
