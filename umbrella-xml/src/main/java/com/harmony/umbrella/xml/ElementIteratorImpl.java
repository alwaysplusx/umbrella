package com.harmony.umbrella.xml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.harmony.umbrella.util.Exceptions;

/**
 * 
 * @author wuxii@foxmail.com
 */
public class ElementIteratorImpl implements ElementIterator {

    // 当前的节点
    private Element element;
    // 当前节点的下级子节点
    private NodeList nodeList;
    private ElementIterator parent;
    // 迭代指针
    private int cursor;
    // 迭代路径
    private String path;

    public ElementIteratorImpl(Element element) {
        this(element, null);
    }
    
    public ElementIteratorImpl(Element element, ElementIterator parent) {
        this.element = element;
        this.nodeList = element.getChildNodes();
        this.parent = parent;
    }

    @Override
    public boolean hasNext() {
        for (int max = nodeList.getLength(); cursor < max; cursor++) {
            if (XmlUtil.isElement(nodeList.item(cursor))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ElementIterator next() {
        if (hasNext()) {
            return new ElementIteratorImpl((Element) nodeList.item(cursor++), this);
        }
        throw new NoSuchElementException("element out of bounds " + cursor);
    }

    @Override
    public Iterator<Element> iterator() {
        List<Element> result = new ArrayList<Element>();
        if (element.hasChildNodes()) {
            NodeList nodeList = element.getChildNodes();
            for (int i = 0, max = nodeList.getLength(); i < max; i++) {
                Node item = nodeList.item(i);
                if (XmlUtil.isElement(item)) {
                    result.add((Element) item);
                }
            }
        }
        return result.iterator();
    }

    @Override
    public void forEach(NodeAcceptor acceptor) {
        for (int i = 0, max = nodeList.getLength(); i < max; i++) {
            Node item = nodeList.item(i);
            if (XmlUtil.isElement(item) && !acceptor.accept(getPath() + item.getNodeName(), item)) {
                break;
            }
        }
    }

    @Override
    public Element getCurrentElement() {
        return element;
    }

    @Override
    public ElementIterator getParent() {
        return parent;
    }

    @Override
    public Element getRootElement() {
        ElementIterator p = this;
        while (p.getParent() != null) {
            p = p.getParent();
        }
        return p.getCurrentElement();
    }

    @Override
    public boolean isLeaf() {
        return XmlUtil.isLeafElement(element);
    }

    @Override
    public void reset() {
        cursor = 0;
    }

    @Override
    public String getPath() {
        if (path == null) {
            StringBuilder pathBuffer = new StringBuilder();

            Node rootNode = getRootElement();
            Node currentNode = getCurrentElement();
            Node parrentNode = element.getParentNode();

            while (!currentNode.isSameNode(rootNode)) {
                String currentName = currentNode.getNodeName();
                NodeList nodes = getNodesByNodeName(parrentNode, currentNode.getNodeName());
                if (nodes != null && nodes.getLength() > 1) {
                    int position = 0;
                    for (int max = nodes.getLength(); position < max; position++) {
                        Node item = nodes.item(position);
                        if (item.isSameNode(currentNode)) {
                            break;
                        }
                    }
                    currentName += "[" + position + "]";
                }
                pathBuffer.insert(0, XmlUtil.PATH_SPLIT + currentName);

                currentNode = parrentNode;
                parrentNode = parrentNode.getParentNode();
            }

            this.path = rootNode.getNodeName() + pathBuffer.toString();
        }
        return path;
    }

    protected NodeList getNodesByNodeName(Node node, String nodeName) {
        XPath xPath = XmlUtil.getXPath();
        try {
            return (NodeList) xPath.evaluate(nodeName, node, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            throw Exceptions.unchecked(e);
        }
        /*return XmlUtil.getElements(item, xpath);
        if (XmlUtil.isElement(node)) {
            return ((Element) node).getElementsByTagName(nodeName);
        } else if (XmlUtil.isDocument(node)) {
            return ((Document) node).getElementsByTagName(nodeName);
        }
        return null;*/
    }

}