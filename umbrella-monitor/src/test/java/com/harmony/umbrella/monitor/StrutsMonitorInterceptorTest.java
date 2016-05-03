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
