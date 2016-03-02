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

import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;

import com.ctc.wstx.stax.WstxInputFactory;

/**
 * @author wuxii@foxmail.com
 */
public class UmbrellaXMLInputFactory extends WstxInputFactory {

    private static final Log log = Logs.getLog(UmbrellaXMLInputFactory.class);

    @Override
    public void setProperty(String propName, Object value) {
        try {
            super.setProperty(propName, value);
        } catch (IllegalArgumentException e) {
            log.info("{}", e.toString());
        }
    }

}
