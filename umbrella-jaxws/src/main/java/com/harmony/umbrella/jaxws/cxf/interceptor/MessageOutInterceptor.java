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
package com.harmony.umbrella.jaxws.cxf.interceptor;

import org.apache.cxf.interceptor.LoggingMessage;
import org.apache.cxf.interceptor.StaxOutInterceptor;
import org.apache.cxf.phase.Phase;

/**
 * @author wuxii@foxmail.com
 */
public class MessageOutInterceptor extends AbstractMessageInterceptor {

    public MessageOutInterceptor() {
        this(Phase.PRE_STREAM);
    }

    public MessageOutInterceptor(String phase) {
        super(phase);
        addBefore(StaxOutInterceptor.class.getName());
    }

    @Override
    protected void logging(LoggingMessage loggingMessage) {
        System.out.println();
        System.out.println("<<<<<<<<<<<<< " + MessageOutInterceptor.class.getName());
        System.out.println(loggingMessage);
        System.out.println("<<<<<<<<<<<<<");
        System.out.println();
    }

}
