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

import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.events.StartElement;

import org.codehaus.stax2.ri.Stax2EventFactoryImpl;

import com.ctc.wstx.compat.QNameCreator;
import com.ctc.wstx.evt.SimpleStartElement;

/**
 * @author wuxii@foxmail.com
 */
public class UmbrellaEventFactory extends Stax2EventFactoryImpl {

    public UmbrellaEventFactory() {
    }

    @Override
    protected QName createQName(String nsURI, String localName) {
        return new QName(nsURI, localName);
    }

    @Override
    protected QName createQName(String nsURI, String localName, String prefix) {
        return QNameCreator.create(nsURI, localName, prefix);
    }

    @SuppressWarnings("rawtypes")
    protected StartElement createStartElement(QName name, Iterator attr, Iterator ns, NamespaceContext ctxt) {
        return SimpleStartElement.construct(mLocation, name, attr, ns, ctxt);
    }
}
