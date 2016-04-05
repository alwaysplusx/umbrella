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
package com.harmony.umbrella.util;

import java.io.File;
import java.io.FileInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.harmony.umbrella.io.Resource;
import com.harmony.umbrella.io.ResourceLoader;
import com.harmony.umbrella.io.ResourceManager;

/**
 * @author wuxii@foxmail.com
 */
public class XmlUtilTest {

    private static DocumentBuilder documentBuilder;
    private static ResourceLoader resourceLoader = ResourceManager.getInstance();

    @Before
    public void setUp() throws Exception {
        documentBuilder = XmlUtil.newDocumentBuilder(false);
    }

    @Test
    public void testParseFile() throws Exception {
        Document document = documentBuilder.parse(new File("src/test/resources/log4j.xml"));
        System.out.println(document);
    }

    @Test
    public void testParseInputStream() throws Exception {
        Resource resource = resourceLoader.getResource("classpath:/log4j.xml");
        Document document = documentBuilder.parse(resource.getInputStream());
        System.out.println(document);
    }

    @Test
    public void testParseInputSource() throws Exception {
        Resource resource = resourceLoader.getResource("classpath:/log4j.xml");
        Document document = documentBuilder.parse(new InputSource(resource.getInputStream()));
        System.out.println(document);
    }

    public static void main(String[] args) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new FileInputStream(new File("src/test/resources/log4j.xml")));
        System.out.println(doc);
    }

}
