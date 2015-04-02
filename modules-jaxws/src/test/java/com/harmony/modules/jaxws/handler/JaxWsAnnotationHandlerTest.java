package com.harmony.modules.jaxws.handler;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.harmony.modules.jaxws.HelloService;
import com.harmony.modules.jaxws.JaxWsContextHandler;
import com.harmony.modules.jaxws.SimpleJaxWsContext;

public class JaxWsAnnotationHandlerTest {

    private JaxWsContextHandler handler;

    @Before
    public void setUp() throws Exception {
        handler = new JaxWsAnnotationHandler();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() throws Exception {
        SimpleJaxWsContext context = new SimpleJaxWsContext(HelloService.class, "sayHi", new Object[] { "wuxii" });
        handler.preExecute(context);
    }

}
