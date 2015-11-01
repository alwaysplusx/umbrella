/*
 * Copyright 2002-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

import com.harmony.umbrella.xpath.XPathUtil;

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
        XPathUtil pathUtil = new XPathUtil(document);
        String attribute = pathUtil.getAttribute("resources/resource[@name='harmony']");
        System.out.println(attribute);
    }

}
