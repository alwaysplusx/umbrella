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
