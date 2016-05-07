package com.harmony.umbrella.xml;

import org.w3c.dom.Node;

/**
 * @author wuxii@foxmail.com
 */
public interface NodeAcceptor {

    boolean accept(String path, Node node);

}
