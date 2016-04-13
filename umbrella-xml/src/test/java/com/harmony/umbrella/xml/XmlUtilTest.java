/*
 * Copyright 2002-2014 the original author or authors.
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

import org.dom4j.Attribute;
import org.dom4j.DocumentFactory;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
    public void testDocument() {
        XmlUtil.forEachElement(doc, new NodeVisitor() {
            @Override
            public void visitElement(String path, Element element) {
                System.out.println(path);
            }
        });
    }

    @Test
    public void testGetElement() throws Exception {
        Element element = XmlUtil.getElement(doc, "objects/users/user");

        XmlUtil.forEachElement(element, new NodeVisitor() {
            @Override
            public void visitElement(String path, Element element) {
                System.out.println(path);
            }
        });
    }

    @Test
    public void testGetAttribute() throws Exception {
        String name = XmlUtil.getAttribute(doc, "objects/users/self/@type");
        System.out.println(name);
    }

    @Test
    public void testXPath() throws Exception {
        SAXReader saxReader = new SAXReader(DocumentFactory.getInstance());
        org.dom4j.Document documents = saxReader.read("src/test/resources/objects.xml");
        Node node = documents.selectSingleNode("objects/users/self/@type");
        String value = ((Attribute) node).getValue();
        System.out.println(value);
    }
}
