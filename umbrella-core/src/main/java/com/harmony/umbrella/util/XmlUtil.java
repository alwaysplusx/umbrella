package com.harmony.umbrella.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * XML工具类.
 */
public class XmlUtil {

    public static boolean hasElement(Document document, String xpath) {
        return false;
    }

    public static Element getElement(Document document, String xpath) throws XPathExpressionException {
        Object obj = getXPath().evaluate(xpath, document, XPathConstants.NODE);
        if (obj instanceof Element) {
            return (Element) obj;
        }
        return null;
    }

    public static Element[] getElements(Document document, String xpath) throws XPathExpressionException {
        Object obj = getXPath().evaluate(xpath, document, XPathConstants.NODESET);
        if (obj instanceof NodeList) {
            NodeList nodeList = ((NodeList) obj);
            Element[] result = new Element[nodeList.getLength()];
            for (int i = 0, max = nodeList.getLength(); i < max; i++) {
                result[i] = (Element) nodeList.item(i);
            }
            return result;
        }
        return new Element[0];
    }

    public static void parser(String path, DefaultHandler handler) throws SAXException, IOException, ParserConfigurationException {
        getSAXParser().parse(path, handler);
        getSAXParser().getXMLReader();
    }

    public static XPath getXPath() {
        return XPathFactory.newInstance().newXPath();
    }

    public static SAXParser getSAXParser() throws ParserConfigurationException, SAXException {
        return SAXParserFactory.newInstance().newSAXParser();
    }

    public static Document newDocument() throws ParserConfigurationException {
        return newDocumentBuilder(true).newDocument();
    }

    public static Document getDocument(String path, boolean ignore) throws IOException, SAXException, ParserConfigurationException {
        return newDocumentBuilder(ignore).parse(path);
    }

    public static DocumentBuilder newDocumentBuilder(boolean ignore) throws ParserConfigurationException {
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        if (ignore) {
            documentBuilder.setEntityResolver(new IgnoreDTDEntityResolver());
        }
        return documentBuilder;
    }

    private static final class IgnoreDTDEntityResolver implements EntityResolver {

        @Override
        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
            return new InputSource(new ByteArrayInputStream("<?xml version='1.0' encoding='UTF-8'?>".getBytes()));
        }

    }

}
