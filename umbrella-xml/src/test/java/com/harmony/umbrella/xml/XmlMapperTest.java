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
}
