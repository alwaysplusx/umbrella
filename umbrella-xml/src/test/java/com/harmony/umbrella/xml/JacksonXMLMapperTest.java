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

import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.util.Date;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator.Feature;
import com.harmony.umbrella.excel.User;

/**
 * @author wuxii@foxmail.com
 */
public class JacksonXMLMapperTest {

    // public static final int MIN_VALUE = -999_999_999;
    
    static XMLInputFactory factory = XMLInputFactory.newFactory();
    static XMLStreamReader reader;
    static {
        try {
            reader = factory.createXMLStreamReader(new FileInputStream(new File("src/test/resources/objects.xml")));
        } catch (Exception e) {
        }
    }

    public static void main(String[] args) throws Exception {
        XmlMapper mapper = new XmlMapper();
        mapper.enable(Feature.WRITE_XML_1_1);
        Customer customer = new Customer(1l, "wuxii");
        customer.setUser(new User("david", 18, new Date()));
        /*mapper.writeValue(new FileOutputStream("./target/customer.xml"), customer);*/
        ObjectWriter writer = mapper.writer();
        System.out.println(writer.writeValueAsString(customer));
        StringReader sr = new StringReader(writer.writeValueAsString(customer));
        Customer value = mapper.readValue(sr, Customer.class);
        System.out.println(value);
    }

    @Test
    public void testXMLReader() throws Exception {
        XmlMapper mapper = new XmlMapper();
        mapper.enable(Feature.WRITE_XML_1_1);
        Object value = mapper.readValue("./target/customer.xml", Customer.class);
        System.out.println(value);
    }

    @Test
    public void testReader() throws Exception {
        XMLStreamReader reader = factory.createXMLStreamReader(new FileInputStream("./target/customer.xml"));
        while (reader.hasNext()) {
            reader.next();
        }
    }

}
