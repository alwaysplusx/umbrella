package com.harmony.umbrella.xml;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author wuxii@foxmail.com
 */
public class XMLMapperTest {

    private static Document doc;

    @BeforeClass
    public static void beforeClass() throws Exception {
        doc = XmlUtil.getDocument("src/test/resources/bean.xml", true);
    }

    @Test
    public void testMapper() {
        XmlBeanMapper mapper = new XmlBeanMapper() {

            @Override
            protected Class<?> getMappedType() {
                return Bean.class;
            }

            @Override
            protected void setMemberValue(Object target, String fieldPath, Element element) {
                System.out.println(fieldPath);
            }

        };

        XmlMapper.mapping(doc.getDocumentElement(), mapper);

        System.out.println(mapper.getResult());
    }

    public static class Bean {

        private String name;
        private List<Bean> beans;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Bean> getBeans() {
            return beans;
        }

        public void setBeans(List<Bean> beans) {
            this.beans = beans;
        }

    }
}
