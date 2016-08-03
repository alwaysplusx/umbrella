package com.harmony.umbrella.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathException;
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
public class XmlUtils {

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
    public static boolean hasElement(Node node, String xpath) {
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
        return getChildElementList(node).iterator();
    }

    static List<Element> getChildElementList(Node node) {
        NodeList nodes = node.getChildNodes();
        List<Element> elements = new ArrayList<Element>();
        for (int i = 0, max = nodes.getLength(); i < max; i++) {
            Node item = nodes.item(i);
            if (isElement(item)) {
                elements.add((Element) item);
            }
        }
        return elements;
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
    public static boolean hasChildElements(Element element) {
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
    public static Element getElement(Node item, String xpath) throws XPathException {
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
     * @throws XPathException
     */
    public static Element[] getElements(Node item, String xpath) throws XPathException {
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
    public static String getAttribute(Node item, String xpath) throws XPathException {
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
     * @throws Exception
     */
    public static Document getDocument(String pathname, boolean ignore) throws Exception {
        return newDocumentBuilder(ignore).parse(pathname);
    }

    /**
     * 由字节数组构建xml document
     * 
     * @param buff
     *            字节数组
     * @param ignore
     *            是否忽略dtd的校验
     * @return
     * @throws Exception
     */
    public static Document getDocument(byte[] buff, boolean ignore) throws Exception {
        return getDocument(new ByteArrayInputStream(buff), ignore);
    }

    /**
     * 由输入流构建xml document
     * 
     * @param is
     *            输入流
     * @param ignore
     *            属否忽略dtd文件头
     * @return
     */
    public static Document getDocument(InputStream is, boolean ignore) throws Exception {
        return newDocumentBuilder(ignore).parse(is);
    }

    /**
     * 创建xml builder
     * 
     * @param ignore
     *            忽略xml校验
     * @return
     * @throws Exception
     */
    public static DocumentBuilder newDocumentBuilder(boolean ignore) throws Exception {
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        if (ignore) {
            documentBuilder.setEntityResolver(new IgnoreDTDEntityResolver());
        }
        return documentBuilder;
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
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws XmlException
     */
    public static SAXParser getSAXParser() throws Exception {
        return SAXParserFactory.newInstance().newSAXParser();
    }

    public static String toXML(Node node) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(node), new StreamResult(writer));
        return writer.toString();
    }

    private static final class IgnoreDTDEntityResolver implements EntityResolver {

        @Override
        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
            return new InputSource(new ByteArrayInputStream("<?xml version='1.0' encoding='UTF-8'?>".getBytes()));
        }

    }
}
