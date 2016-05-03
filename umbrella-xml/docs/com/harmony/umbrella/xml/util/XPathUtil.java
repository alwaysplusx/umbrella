package com.harmony.umbrella.xml.util;

import java.util.HashMap;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.harmony.umbrella.util.Assert;

/**
 * @author wuxii@foxmail.com
 */
public class XPathUtil {

    private Document document;

    private XPathFactory xPathFactory;

    private XPath xpath;

    public XPathUtil(Document document) {
        Assert.notNull(document, "document must not be null");
        this.document = document;
        this.xPathFactory = XPathFactory.newInstance();
        this.xpath = xPathFactory.newXPath();
    }

    public Map<String, String> getAttributes(String expression) throws XPathException {
        Map<String, String> attributes = new HashMap<String, String>();
        Element element = getElement(expression);
        NamedNodeMap nnm = element.getAttributes();
        for (int i = 0, max = nnm.getLength(); i < max; i++) {
            Node item = nnm.item(i);
            attributes.put(item.getNodeName(), item.getNodeValue());
        }
        return attributes;
    }

    public Element getElement(String expression) throws XPathException {
        Object obj = xpath.evaluate(expression, document, XPathConstants.NODE);
        if (obj instanceof Element) {
            return (Element) obj;
        }
        throw new XPathException(expression + " is not element path expression");
    }

    public Element[] getElements(String expression) throws XPathException {
        Object obj = xpath.evaluate(expression, document, XPathConstants.NODESET);
        if (obj instanceof NodeList) {
            NodeList nodeList = ((NodeList) obj);
            Element[] result = new Element[nodeList.getLength()];
            for (int i = 0, max = nodeList.getLength(); i < max; i++) {
                result[i] = (Element) nodeList.item(i);
            }
            return result;
        }
        throw new XPathException(expression + " is not elements path expression");
    }
}
