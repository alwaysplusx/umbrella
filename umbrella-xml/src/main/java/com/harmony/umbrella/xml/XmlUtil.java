package com.harmony.umbrella.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
     * 循环遍历document的所有element
     * 
     * @param document
     *            xml document
     * @param visitor
     *            NodeVisitor
     */
    public static void forEachElement(Document document, NodeVisitor visitor) {
        forEachElement(document.getDocumentElement(), visitor);
    }

    /**
     * 循环遍历element的所有子element, 包括自身
     * 
     * @param element
     * @param visitor
     */
    public static void forEachElement(Element element, NodeVisitor visitor) {
        _forEachElement(element.getTagName(), element, visitor);
    }

    private static void _forEachElement(String path, Element element, NodeVisitor visitor) {
        visitor.visitElement(path, element);
        NodeList nodeList = element.getChildNodes();
        Map<String, Integer> tagCountMap = new HashMap<String, Integer>();
        Map<String, Integer> repeatCountMap = new HashMap<String, Integer>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (isElement(node)) {
                Element e = (Element) node;
                String tagName = e.getTagName();
                Integer tagCount = tagCountMap.get(tagName);
                if (tagCount == null) {
                    // 同名称的节点个数
                    tagCountMap.put(tagName, tagCount = countElement(element, tagName));
                }
                StringBuilder sb = new StringBuilder();
                sb.append(path).append(PATH_SPLIT).append(tagName);
                if (tagCount > 1) {
                    Integer repeatCount = repeatCountMap.get(tagName);
                    repeatCountMap.put(tagName, repeatCount == null ? repeatCount = 0 : repeatCount++);
                    sb.append("[").append(repeatCount).append("]");
                }
                _forEachElement(sb.toString(), e, visitor);
            }
        }
    }

    /**
     * 获取element的所有下级子element
     * 
     * @param element
     *            dom节点
     * @return 所有下级子节点
     */
    public static Iterator<Element> getChildElements(Element element) {
        NodeList nodes = element.getChildNodes();
        List<Element> elements = new ArrayList<Element>();
        for (int i = 0, max = nodes.getLength(); i < max; i++) {
            Node node = nodes.item(i);
            if (isElement(node)) {
                elements.add((Element) node);
            }
        }
        return elements.iterator();
    }

    /**
     * 检查xpath下是否存在element
     * 
     * @param document
     *            xml document
     * @param xpath
     *            xpath expression
     * @return
     * @throws XPathExpressionException
     */
    public static boolean hasElement(Document document, String xpath) throws XPathExpressionException {
        return getElement(document, xpath) != null;
    }

    /**
     * 判断对象是否是element
     * 
     * @param node
     * @return
     */
    public static boolean isElement(Object node) {
        return node instanceof Element;
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
    public static int countElement(Object item, String xpath) {
        try {
            return getElements(item, xpath).length;
        } catch (XPathExpressionException e) {
            return -1;
        }
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
