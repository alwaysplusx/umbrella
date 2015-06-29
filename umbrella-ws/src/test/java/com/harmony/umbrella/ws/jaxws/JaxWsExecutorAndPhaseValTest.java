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
package com.harmony.umbrella.ws.jaxws;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import com.harmony.umbrella.ws.Handler;
import com.harmony.umbrella.ws.Handler.HandleMethod;
import com.harmony.umbrella.ws.Phase;
import com.harmony.umbrella.ws.WebServiceAbortException;
import com.harmony.umbrella.ws.services.HelloService;
import com.harmony.umbrella.ws.services.HelloWebService;
import com.harmony.umbrella.ws.support.SimpleContext;
import com.harmony.umbrella.ws.visitor.PhaseValidationVisitor;

/**
 * @author wuxii@foxmail.com
 */
public class JaxWsExecutorAndPhaseValTest {

    private static final String address = "http://localhost:8081/hello";
    private static final JaxWsExecutor executor = new JaxWsCXFExecutor();

    private static int count = 0;

    @BeforeClass
    public static void setUp() {
        JaxWsServerBuilder.create().publish(HelloWebService.class, address);
    }

    @Test
    public void testHelloServicePhaseVal() {
        SimpleContext context = new SimpleContext(HelloService.class, "sayHi", new Object[] { "wuxii" });
        context.setAddress(address);
        Object result = executor.execute(context, new PhaseValidationVisitor());
        assertNotNull(result);
        assertEquals("Hi wuxii", result);
        assertEquals(3, count);
    }

    @Handler(HelloService.class)
    public static class HelloServiceSayHiPhaseValidation {

        @HandleMethod(phase = Phase.PRE_INVOKE)
        public boolean sayHi(String message, Map<String, Object> content) {
            count++;
            return true;
        }

        @HandleMethod(phase = Phase.ABORT)
        public void sayHi(WebServiceAbortException exception, String message, Map<String, Object> content) {
        }

        @HandleMethod(phase = Phase.POST_INVOKE)
        public void sayHi(String result, String message, Map<String, Object> content) {
            count++;
        }

        @HandleMethod(phase = Phase.THROWING)
        public void sayHi(Throwable e, String message, Map<String, Object> content) {
        }

        @HandleMethod(phase = Phase.FINALLY)
        public void sayHi(Throwable e, String result, String message, Map<String, Object> content) {
            count++;
        }
    }

    public static void main(String[] args) {
        JaxWsServerBuilder.create().publish(HelloWebService.class, address);
    }

}
