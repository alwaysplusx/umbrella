package com.harmony.umbrella.xml;

import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 * @author wuxii@foxmail.com
 */
public class ElementIteratorTest {

    private static Document doc;

    @BeforeClass
    public static void beforeClass() throws Exception {
        doc = XmlUtil.getDocument("src/test/resources/objects.xml", true);
    }

    @Test
    public void testIterator() {
        ElementIterator eia = new ElementIterator(doc.getDocumentElement());
        iterator(eia);
    }

    public void iterator(ElementIterator eia) {
        ElementContext ec = eia.getElementContext();
        System.out.print(ec.getPath());
        if (!ec.hasChirdElements()) {
            System.out.print(", " + ec.getElementValue());
        }
        System.out.println();
        while (eia.hasNext()) {
            iterator(eia.next());
        }
    }

}
