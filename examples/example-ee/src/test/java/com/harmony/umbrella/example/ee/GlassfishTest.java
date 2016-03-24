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
package com.harmony.umbrella.example.ee;

import static org.junit.Assert.*;

import java.util.Properties;

import javax.ejb.EJB;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.harmony.umbrella.examples.ee.I;
import com.harmony.umbrella.test.ContainerConfiguration;
import com.harmony.umbrella.test.EJBJUnit4ClassRunner;

/**
 * @author wuxii@foxmail.com
 */
@RunWith(EJBJUnit4ClassRunner.class)
@ContainerConfiguration(location = "classpath:/glassfish.properties")
public class GlassfishTest {

    @EJB
    private I i;

    @Test
    public void testI() {
        assertNotNull(i);
    }

    public static void main(String[] args) throws NamingException {
        // glassfish default port value will be 3700,
        Properties props = new Properties();
        props.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.enterprise.naming.SerialInitContextFactory");
        props.setProperty("org.omg.CORBA.ORBInitialHost", "localhost");
        props.setProperty("org.omg.CORBA.ORBInitialPort", "3700");
        InitialContext ctx = new InitialContext(props);
        Object lookup = ctx.lookup("java:global/example-ee/B!com.harmony.umbrella.examples.ee.I");
        System.out.println(lookup);
    }

}
