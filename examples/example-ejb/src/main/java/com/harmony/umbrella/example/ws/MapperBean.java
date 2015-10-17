/*
 * Copyright 2002-2015 the original author or authors.
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
package com.harmony.umbrella.example.ws;

import java.util.ServiceLoader;

import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.xml.stream.XMLInputFactory;

import com.harmony.umbrella.example.entity.Bar;
import com.harmony.umbrella.example.entity.Foo;
import com.harmony.umbrella.mapper.BeanMapper;
import com.harmony.umbrella.util.ClassUtils;
import com.harmony.umbrella.ws.ser.Message;
import com.harmony.umbrella.ws.ser.ServerSupport;

/**
 * @author wuxii@foxmail.com
 */
@Stateless(mappedName = "MapperBean")
@WebService(serviceName = "MapperBean")
public class MapperBean extends ServerSupport {

    public Message mapper(Foo foo) {
        BeanMapper mapper = BeanMapper.getInstance("mapping.xml");
        Bar bar = mapper.mapper(foo, Bar.class, "foo2bar");
        System.out.println(bar);
        return success();
    }

    public String getXMLInputFactoryName() {
        XMLInputFactory factory = XMLInputFactory.newFactory();

        boolean result = setRestrictionProperties(factory);

        LOG.info("default XMLInputFactory {} set properties result {}", factory, result);

        factory = XMLInputFactory.newFactory(XMLInputFactory.class.getName(), ClassUtils.getDefaultClassLoader());

        result = setRestrictionProperties(factory);

        LOG.info("current context classLoader XMLInputFactory {} set properties result {}", factory, result);

        factory = new com.ctc.wstx.stax.WstxInputFactory();

        result = setRestrictionProperties(factory);

        LOG.info("use new XMLInputFactory {} set properties result {}", factory, result);

        ServiceLoader<XMLInputFactory> loader = ServiceLoader.load(XMLInputFactory.class);
        loader.reload();

        for (XMLInputFactory provider : loader) {
            System.out.println(provider);
        }

        return factory == null ? "" : factory.getClass().getName();
    }

    private static boolean setRestrictionProperties(XMLInputFactory factory) {
        return setProperty(factory, "com.ctc.wstx.maxAttributesPerElement", 1)//
                && setProperty(factory, "com.ctc.wstx.maxAttributeSize", 1)//
                && setProperty(factory, "com.ctc.wstx.maxChildrenPerElement", 1)//
                && setProperty(factory, "com.ctc.wstx.maxElementCount", 1)//
                && setProperty(factory, "com.ctc.wstx.maxElementDepth", 1)//
                && setProperty(factory, "com.ctc.wstx.maxCharacters", 1) //
                && setProperty(factory, "com.ctc.wstx.maxTextLength", 1);
    }

    private static boolean setProperty(XMLInputFactory f, String p, Object o) {
        try {
            f.setProperty(p, o);
            return true;
        } catch (Throwable t) {
            // ignore
        }
        return false;
    }
}
