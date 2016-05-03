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
