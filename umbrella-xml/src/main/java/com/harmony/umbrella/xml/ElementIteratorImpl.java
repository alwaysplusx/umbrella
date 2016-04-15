package com.harmony.umbrella.xml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * @author wuxii@foxmail.com
 */
public class ElementIteratorImpl implements ElementIterator {

    private Element element;
    private NodeList nodeList;
    private ElementIterator parent;
    private int cursor;
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
        throw new ArrayIndexOutOfBoundsException("element out of bounds " + cursor);
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
    public Element getCurrent() {
        return element;
    }

    @Override
    public ElementIterator getParent() {
        return parent;
    }

    @Override
    public Element getRoot() {
        ElementIterator p = this;
        while (p.getParent() != null) {
            p = p.getParent();
        }
        return p.getCurrent();
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

            Node rootNode = getRoot();
            Node currentNode = getCurrent();
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
                pathBuffer.insert(0, currentName + XmlUtil.PATH_SPLIT);

                currentNode = parrentNode;
                parrentNode = parrentNode.getParentNode();
            }

            this.path = rootNode.getNodeName() + XmlUtil.PATH_SPLIT + pathBuffer.toString();
        }
        return path;
    }

    private NodeList getNodesByNodeName(Node node, String nodeName) {
        if (XmlUtil.isElement(node)) {
            return ((Element) node).getElementsByTagName(nodeName);
        } else if (XmlUtil.isDocument(node)) {
            return ((Document) node).getElementsByTagName(nodeName);
        }
        return null;
    }
}