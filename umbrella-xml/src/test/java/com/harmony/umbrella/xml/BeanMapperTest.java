package com.harmony.umbrella.xml;

import java.util.ArrayList;

import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author wuxii@foxmail.com
 */
public class BeanMapperTest {

    private static Document doc;
    private static BeanMapper mapper;

    @BeforeClass
    public static void beforeClass() throws Exception {
        doc = XmlUtil.getDocument("src/test/resources/bean.xml", true);
        mapper = new BeanMapper();
    }

    @Test
    public void testMapper() {
        Element root = doc.getDocumentElement();
        Bean bean = mapper.mapping(root, Bean.class);
        System.out.println(bean);
    }

    public static class Bean {
        private String name;
        private ArrayList<Bean> beans;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public ArrayList<Bean> getBeans() {
            return beans;
        }

        public void setBeans(ArrayList<Bean> beans) {
            this.beans = beans;
        }

    }

}
