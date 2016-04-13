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

import org.w3c.dom.Element;

import com.harmony.umbrella.util.Converter;
import com.harmony.umbrella.util.ReflectionUtils;

/**
 * @author wuxii@foxmail.com
 */
public class XmlMapper {

    public static Object mapper(Element element) {
        return null;
    }

    /**
     * 将xml的节点映射为单个java对象
     * 
     * @param element
     *            xml节点
     * @param mapperType
     *            映射的java类型
     * @return
     */
    public static <T> T mapper(Element element, Class<T> mapperType) {
        XmlBeanMapper<T> visitor = new SingleJavaBeanMapper<T>(mapperType);
        XmlUtil.forEachElement(element, visitor);
        return visitor.getResult();
    }

    private static final class SingleJavaBeanMapper<T> extends XmlBeanMapper<T> {

        public SingleJavaBeanMapper(Class<T> mapperType) {
            super(mapperType);
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
        @Override
        protected void setTargetValue(String path, Element element) {
            Object target = getPathTarget(path);
            Object content = element.getTextContent();
            Class<?> fieldType = getFieldType(target, element.getTagName());
            Converter converter = getCustomeConvert(element, fieldType);
            if (converter != null) {
                content = converter.convert(content);
            }
            ReflectionUtils.setFieldValue(element.getTagName(), target, content);
        }

    }

}
