package com.harmony.umbrella.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * xml工具类
 * 
 * @author wuxii@foxmail.com
 */
public class XmlUtil {

    public static final String PATH_SPLIT = "/";

    /**
     * 将document包装为element iterator迭代器
     * 
     * @param document
     *            document
     * @return elementIterator
     */
    public static ElementIterator iterator(Document document) {
        return new ElementIteratorImpl(document.getDocumentElement());
    }

    /**
     * 将element包装为element iterator迭代器
     * 
     * @param element
     *            element
     * @return elementIterator
     */
    public static ElementIterator iterator(Element element) {
        return new ElementIteratorImpl(element);
    }

    /**
     * 迭代doument下的所有element
     * 
     * @param document
     *            document
     * @param acceptor
     *            element接收
     */
    public static void iterator(Document document, NodeAcceptor acceptor) {
        iterator(document.getDocumentElement(), acceptor);
    }

    /**
     * 迭代element下的所有element
     * 
     * @param element
     *            element
     * @param acceptor
     *            element接收
     */
    public static void iterator(Element element, NodeAcceptor acceptor) {
        iterator(iterator(element), acceptor);
    }

    private static boolean iterator(ElementIterator eit, NodeAcceptor acceptor) {
        if (!acceptor.accept(eit.getPath(), eit.getCurrent())) {
            return false;
        }
        while (eit.hasNext()) {
            if (!iterator(eit.next(), acceptor)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 循环遍历node下一级的所有node, 通过下层的节点acceptor的返回值{@code false}可以中断遍历
     * 
     * @param node
     *            待遍历的node
     * @param acceptor
     *            下个节点的受理
     */
    public static void forEach(Node node, NodeAcceptor acceptor) {
        NodeList nodes = node.getChildNodes();
        for (int i = 0, max = nodes.getLength(); i < max; i++) {
            Node item = nodes.item(i);
            if (!acceptor.accept(item.getNodeName(), item)) {
                break;
            }
        }
    }

    /**
     * 检查xpath下是否存在element
     * 
     * @param document
     *            xml node
     * @param xpath
     *            xpath expression
     * @return
     * @throws XPathExpressionException
     */
    public static boolean hasElement(Node node, String xpath) throws XPathExpressionException {
        return countElement(node, xpath) > 0;
    }

    /**
     * 判断对象是否是element
     * 
     * @param node
     *            节点
     * @return
     */
    public static boolean isElement(Object node) {
        return node instanceof Node && ((Node) node).getNodeType() == Node.ELEMENT_NODE;
    }

    /**
     * 判断节点是否是attribute
     * 
     * @param node
     *            节点
     * @return
     */
    public static boolean isAttribute(Object node) {
        return node instanceof Node && ((Node) node).getNodeType() == Node.ATTRIBUTE_NODE;
    }

    /**
     * 判断阶段是否是document
     * 
     * @param node
     *            节点
     * @return
     */
    public static boolean isDocument(Object node) {
        return node instanceof Node && ((Node) node).getNodeType() == Node.DOCUMENT_NODE;
    }

    /**
     * 获取element的所有下一级element
     * 
     * @param node
     *            dom节点
     * @return 所有下级子节点
     */
    public static Iterator<Element> getChildElements(Node node) {
        NodeList nodes = node.getChildNodes();
        List<Element> elements = new ArrayList<Element>();
        for (int i = 0, max = nodes.getLength(); i < max; i++) {
            Node item = nodes.item(i);
            if (isElement(item)) {
                elements.add((Element) item);
            }
        }
        return elements.iterator();
    }

    /**
     * 统计item下对应xpath的element一共有多少个
     * 
     * @param item
     *            dom
     * @param xpath
     *            xpath expression
     * @return
     */
    public static int countElement(Node node, String xpath) {
        int count = 0;
        try {
            NodeList nodes = (NodeList) getXPath().evaluate(xpath, node, XPathConstants.NODESET);
            if (nodes.getLength() > 0) {
                for (int i = 0, max = nodes.getLength(); i < max; i++) {
                    Node item = nodes.item(i);
                    if (isElement(item)) {
                        count++;
                    }
                }
            }
            return count;
        } catch (XPathExpressionException e) {
            return -1;
        }
    }

    /**
     * 判断element是否是最终节点
     * 
     * @param element
     * @return
     */
    public static boolean isLeafElement(Element element) {
        NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            if (isElement(nodeList.item(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获得dom指定xpath下的element
     * 
     * @param item
     *            dom
     * @param xpath
     *            xpath expression
     * @return
     * @throws XPathExpressionException
     */
    public static Element getElement(Object item, String xpath) throws XPathExpressionException {
        Object obj = getXPath().evaluate(xpath, item, XPathConstants.NODE);
        return isElement(obj) ? (Element) obj : null;
    }

    /**
     * 获的dom下指定xpaht的所有element
     * 
     * @param item
     *            dom
     * @param xpath
     *            xpath expression
     * @return
     * @throws XPathExpressionException
     */
    public static Element[] getElements(Object item, String xpath) throws XPathExpressionException {
        Object obj = getXPath().evaluate(xpath, item, XPathConstants.NODESET);
        List<Element> elements = new ArrayList<Element>();
        if (obj instanceof NodeList) {
            NodeList nodeList = ((NodeList) obj);
            if (nodeList.getLength() > 0) {
                for (int i = 0, max = nodeList.getLength(); i < max; i++) {
                    Node node = nodeList.item(i);
                    if (isElement(node)) {
                        elements.add((Element) node);
                    }
                }
            }
        }
        return elements.toArray(new Element[elements.size()]);
    }

    /**
     * 根据xpath获取节点的属性值
     * 
     * @param item
     *            节点
     * @param xpath
     *            xpath expression
     * @return
     * @throws XPathExpressionException
     */
    public static String getAttribute(Object item, String xpath) throws XPathExpressionException {
        Object obj = getXPath().evaluate(xpath, item, XPathConstants.STRING);
        return obj instanceof String ? (String) obj : null;
    }

    /**
     * 从指定路径中加载xml文件
     * 
     * @param pathname
     *            指定路径
     * @param ignore
     *            是否忽略xml的校验
     * @return
     * @throws XmlException
     */
    public static Document getDocument(String pathname, boolean ignore) throws XmlException {
        try {
            return newDocumentBuilder(ignore).parse(pathname);
        } catch (Exception e) {
            throw new XmlException(e);
        }
    }

    /**
     * 创建xml builder
     * 
     * @param ignore
     *            忽略xml校验
     * @return
     * @throws XmlException
     */
    public static DocumentBuilder newDocumentBuilder(boolean ignore) throws XmlException {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            if (ignore) {
                documentBuilder.setEntityResolver(new IgnoreDTDEntityResolver());
            }
            return documentBuilder;
        } catch (ParserConfigurationException e) {
            throw new XmlException(e);
        }
    }

    /**
     * 创建xpath工具
     * 
     * @return
     */
    public static XPath getXPath() {
        return XPathFactory.newInstance().newXPath();
    }

    /**
     * 创建saxParse工具
     * 
     * @return
     * @throws XmlException
     */
    public static SAXParser getSAXParser() throws XmlException {
        try {
            return SAXParserFactory.newInstance().newSAXParser();
        } catch (Exception e) {
            throw new XmlException(e);
        }
    }

    private static final class IgnoreDTDEntityResolver implements EntityResolver {

        @Override
        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
            return new InputSource(new ByteArrayInputStream("<?xml version='1.0' encoding='UTF-8'?>".getBytes()));
        }

    }
}
