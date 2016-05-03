package com.harmony.umbrella.xml;

import java.util.Arrays;
import java.util.Iterator;

import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author wuxii@foxmail.com
 */
public class XmlUtilTest {

    private static Document doc;

    @BeforeClass
    public static void beforeClass() throws Exception {
        doc = XmlUtil.getDocument("src/test/resources/objects.xml", true);
    }

    @Test
    public void testForEach() {
        XmlUtil.forEach(doc, new ElementAcceptor() {

            @Override
            public boolean acceptElement(String path, Element element) {
                System.out.println(path + ", " + element.getTextContent());
                return true;
            }
        });
    }

    @Test
    public void testIterator() {
        XmlUtil.iterator(doc, new NodeAcceptor() {

            @Override
            public boolean accept(String path, Node node) {
                System.out.println(path);
                return true;// !"objects/users/user[0]".equals(path);
            }
        });
    }

    @Test
    public void testToXML() throws Exception {
        System.out.println(XmlUtil.toXML(XmlUtil.getElement(doc, "objects/users/user[1]")));
    }

    @Test
    public void test() {
        Iterator<Object> iterator = Arrays.asList().iterator();
        iterator.next();
    }

}
