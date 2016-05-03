package com.harmony.umbrella.xml;

import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author wuxii@foxmail.com
 */
public class XmlMapperTest {

    private static Document doc;

    @BeforeClass
    public static void beforeClass() throws Exception {
        System.setProperty("umbrella.log.level", "INFO");
        doc = XmlUtil.getDocument("src/test/resources/objects.xml", true);
    }

    @Test
    public void testMapper() throws Exception {
        Element element = XmlUtil.getElement(doc, "objects/customers/customer");
        XmlMapper.mapping(element, new XmlBeanMapper<Customer>() {

            @Override
            protected boolean setTargetFieldValue(Customer target, String fieldPath, Element element) {
                System.out.println(fieldPath);
                return true;
            }

        });
    }

    @Test
    public void testSingleMapper() throws Exception {
        Element element = XmlUtil.getElement(doc, "objects/customers/customer");
        XmlMapper.mapping(element, new SingleJavaBeanMapper<Customer>(Customer.class));

    }

    public static void main(String[] args) {
        Customer[] cms = new Customer[] { new Customer() };
        Object cs = cms;
        for (Object c : Arrays.asList(Customer[].class.cast(cs))) {
            System.out.println(c);
        }

        System.out.println(cs.getClass());
        System.out.println(Customer[].class);

        for (Object c : Arrays.asList(cs.getClass().cast(cs))) {
            System.out.println(c);
        }
    }
}
