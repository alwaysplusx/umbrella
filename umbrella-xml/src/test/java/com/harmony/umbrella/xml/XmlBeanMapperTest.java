package com.harmony.umbrella.xml;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlList;

import org.junit.Test;
import org.w3c.dom.Document;

import com.harmony.umbrella.util.XmlUtils;
import com.harmony.umbrella.xml.convert.BigDecimalConverter;

/**
 * @author wuxii@foxmail.com
 */
public class XmlBeanMapperTest {

    public static void main(String[] args) throws Exception {
        Document doc = XmlUtils.getDocument("src/test/resources/bean.xml", true);
        Bean bean = new XmlBeanMapper().mapping(doc.getDocumentElement(), Bean.class);
        System.out.println(bean);
    }

    @Test
    public void testMapper() {
        XmlBeanMapper mapper = new XmlBeanMapper();
        mapper.addValueConverter(ValueConverter.class);
        mapper.addValueConverter(BigDecimalConverter.class);
    }

    public static class Bean {
        String name;
        Bean bean;
        @XmlList
        ArrayList<Bean> beans;

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

        public Bean getBean() {
            return bean;
        }

        public void setBean(Bean bean) {
            this.bean = bean;
        }

        @Override
        public String toString() {
            return "Bean [name=" + name + ", bean=" + bean + ", beans=" + beans + "]";
        }

    }

}
