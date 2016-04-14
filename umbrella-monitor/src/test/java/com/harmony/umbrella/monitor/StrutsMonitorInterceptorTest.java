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
package com.harmony.umbrella.monitor;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.harmony.umbrella.util.IOUtils;

/**
 * @author wuxii@foxmail.com
 */
public class StrutsMonitorInterceptorTest {

    public static boolean ACTION_FLAG = false;
    public static boolean INTERCEPTOR_FLAG = false;

    private static Server server;

    @BeforeClass
    public static void beforeClass() throws Exception {
        server = new Server(8080);
        server.setHandler(new WebAppContext("src/test/resources/webapp", "/"));
        server.start();
    }

    @SuppressWarnings("resource")
    @Test
    public void testInterceptor() throws Exception {
        assertTrue(server.isStarted());
        assertFalse(ACTION_FLAG);
        assertFalse(INTERCEPTOR_FLAG);

        URL url = new URL("http://localhost:8080/monitor");
        Object content = url.openConnection().getContent();
        assertTrue(IOUtils.contentEquals((InputStream) content, new FileInputStream("src/test/resources/webapp/index.html")));

        assertTrue(ACTION_FLAG);
        assertTrue(INTERCEPTOR_FLAG);
    }

    @AfterClass
    public static void afterClass() throws Exception {
        server.stop();
    }
}
