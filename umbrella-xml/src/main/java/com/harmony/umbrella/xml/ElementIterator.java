package com.harmony.umbrella.xml;

import java.util.Iterator;

import org.w3c.dom.Element;

/**
 * element迭代器
 * 
 * @author wuxii@foxmail.com
 */
public interface ElementIterator extends Iterable<Element>, Iterator<ElementIterator> {

    /**
     * 判断当前element是否还有下一个子element
     * 
     * @return
     */
    boolean hasNext();

    /**
     * 下一个子element的迭代器
     * 
     * @return
     * @throws NoSuchElementException
     *             不再有下个子element时
     */
    ElementIterator next();

    /**
     * 父层的elementIterator
     * 
     * @return
     */
    ElementIterator getParent();

    /**
     * 下层element的迭代器
     * 
     * @see Iterator
     * @see Iteraable
     */
    Iterator<Element> iterator();

    /**
     * 采用acceptor迭代下层的element
     * 
     * @param acceptor
     */
    void forEach(NodeAcceptor acceptor);

    /**
     * 当前的element
     * 
     * @return
     */
    Element getCurrentElement();

    /**
     * 开始迭代的根节点
     * 
     * @return
     */
    Element getRootElement();

    /**
     * 判断是否为叶子节点, 不再有子element
     * 
     * @return
     */
    boolean isLeaf();

    /**
     * 重置迭代器的顺序
     */
    void reset();

    /**
     * 得出类似于xpath的节点表达式
     * 
     * @return
     */
    String getPath();

    // void forEach(Consumer<Node> consumer);
}
