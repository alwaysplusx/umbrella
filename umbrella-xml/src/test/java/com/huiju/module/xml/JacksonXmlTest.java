package com.huiju.module.xml;

import java.io.FileInputStream;
import java.text.SimpleDateFormat;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.harmony.umbrella.util.IOUtils;

/**
 * @author wuxii@foxmail.com
 */
public class JacksonXmlTest {

    @BeforeClass
    public static void beforeClass() throws Exception {
    }

    @Test
    public void testMapper() throws Exception {
        XmlMapper mapper = new XmlMapper();
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        FileInputStream fis = new FileInputStream("src/test/resources/objects.xml");
        String content = IOUtils.toString(fis);
        Person value = mapper.readValue(content, Person.class);
        System.out.println(value);
        fis.close();
    }

    @AfterClass
    public static void afterClass() {

    }

}
