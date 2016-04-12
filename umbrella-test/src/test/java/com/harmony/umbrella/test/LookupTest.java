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
package com.harmony.umbrella.test;

import static org.junit.Assert.*;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.openejb.client.RemoteInitialContextFactory;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author wuxii@foxmail.com
 */
@Ignore
public class LookupTest {

    @Test
    public void testLookup() throws Exception {
        Properties props = new Properties();
        props.put(Context.INITIAL_CONTEXT_FACTORY, RemoteInitialContextFactory.class.getName());
        props.put(Context.PROVIDER_URL, "ejbd://127.0.0.1:4201");
        InitialContext context = new InitialContext(props);
        SampleRemote sample = (SampleRemote) context.lookup("java:global/umbrella-test/SampleBean!com.harmony.umbrella.test.SampleRemote");
        assertEquals("Hi wuxii", sample.sayHi("wuxii"));
    }

}
