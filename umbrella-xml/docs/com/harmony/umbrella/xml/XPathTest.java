package com.harmony.umbrella.xml;

import java.io.InputStream;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.harmony.umbrella.xml.util.XPathUtil;

/**
 * @author wuxii@foxmail.com
 */
public class XPathTest {

    private static Document document;

    @BeforeClass
    public static void setUpClass() throws Exception {

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();

        document = builder.parse(ClassLoader.getSystemResourceAsStream("resource.xml"));
    }

    public static void main(String[] args) throws Exception {

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();

        InputStream inputStream = ClassLoader.getSystemResourceAsStream("resource.xml");

        Document doc = builder.parse(inputStream);

        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xpath = xPathFactory.newXPath();

        NodeList obj = (NodeList) xpath.evaluate("resources/resource/property", doc, XPathConstants.NODESET);

        for (int i = 0, max = obj.getLength(); i < max; i++) {
            Element element = (Element) obj.item(i);
            System.out.println(element.getAttribute("name"));
        }

    }

    @Test
    public void testXPathUtil() throws Exception {
        XPathUtil pathUtil = new XPathUtil(document);
        Map<String, String> attributes = pathUtil.getAttributes("resources/resource");
        System.out.println(attributes);
    }

    @Test
    public void testGetAttribute() throws Exception {
    }

}
