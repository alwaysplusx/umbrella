package com.harmony.umbrella.xml;

/**
 * @author wuxii@foxmail.com
 */
public class XmlUtilTest {

    /*
    private static Document doc;
    @BeforeClass
    public static void beforeClass() throws Exception {
        doc = XmlUtils.getDocument("src/test/resources/objects.xml", true);
    }

    @Test
    public void testForEach() {
        XmlUtils.forEach(doc, new ElementAcceptor() {

            @Override
            public boolean acceptElement(String path, Element element) {
                System.out.println(path + ", " + element.getTextContent());
                return true;
            }
        });
    }

    @Test
    public void testIterator() {
        iterator(new ElementIterator(doc.getDocumentElement()), new NodeAcceptor() {
            @Override
            public boolean accept(String path, Node node) {
                System.out.println(path);
                return true;
            }
        });
    }

    @Test
    public void testToXML() throws Exception {
        System.out.println(XmlUtil.toXML(XmlUtil.getElement(doc, "objects/users/user[1]")));
    }

    private static boolean iterator(ElementIterator ei, NodeAcceptor acceptor) {
        ElementContext ec = ei.getElementContext();
        if (!acceptor.accept(ec.getPath(), ec.getElement())) {
            return false;
        }
        while (ei.hasNext()) {
            if (!iterator(ei.next(), acceptor)) {
                return false;
            }
        }
        return true;
    }*/
}
