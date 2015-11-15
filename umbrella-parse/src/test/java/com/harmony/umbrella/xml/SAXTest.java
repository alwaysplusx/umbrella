/*
 * Copyright 2002-2015 the original author or authors.
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

import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author wuxii@foxmail.com
 */
public class SAXTest {

    public static void main(String[] args) throws Exception {
        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        SAXParser saxParser = parserFactory.newSAXParser();
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("resource.xml");

        saxParser.parse(inputStream, new DefaultHandler() {

            private String elementName;

            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                elementName = qName;
                System.out.println("uri=" + uri);
                System.out.println("localName=" + localName);
                System.out.println("qName=" + qName);
                for (int i = 0; i < attributes.getLength(); i++) {
                    System.out.println(attributes.getType(i) + ", " + attributes.getQName(i) + ", " + attributes.getValue(i));
                }
                System.out.println();
            }

            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException {
                elementName = null;
                super.endElement(uri, localName, qName);
            }

            @Override
            public void startDocument() throws SAXException {
                super.startDocument();
            }

            @Override
            public void endDocument() throws SAXException {
                super.endDocument();
            }

            @Override
            public void startPrefixMapping(String prefix, String uri) throws SAXException {
                System.out.println(">> " + prefix);
            }

            @Override
            public void endPrefixMapping(String prefix) throws SAXException {
                System.out.println("<< " + prefix);
            }

            @Override
            public void characters(char[] ch, int start, int length) throws SAXException {
                System.out.println(">>> element=" + elementName + ", " + new String(ch, start, length));
            }

            @Override
            public void processingInstruction(String target, String data) throws SAXException {
                System.out.println("target=" + target + ", data=" + data);
            }
        });
    }
}
