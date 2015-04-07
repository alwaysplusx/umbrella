package com.harmony.modules.jaxws.support;

import javax.xml.ws.Endpoint;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.harmony.modules.jaxws.HelloService;
import com.harmony.modules.jaxws.HelloServiceImpl;
import com.harmony.modules.jaxws.SimpleJaxWsContext;

public class JaxWsContextReceiverBeanTest {

    private JaxWsContextReceiver receiver;

    @Before
    public void setUp() throws Exception {
        JaxWsContextReceiverImpl receiver = new JaxWsContextReceiverImpl();
        receiver.setReload(false);
        this.receiver = receiver;
        Endpoint.publish("http://localhost:8080/hi", new HelloServiceImpl());
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() {
        SimpleJaxWsContext context = new SimpleJaxWsContext(HelloService.class, "sayHi", new Object[] { "wuxii" });
        context.setAddress("http://localhost:8080/hi");
        receiver.receive(context);
    }

}
