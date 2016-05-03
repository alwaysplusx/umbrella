package com.harmony.umbrella.ws;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Properties;

import javax.ejb.EJB;
import javax.ejb.embeddable.EJBContainer;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.harmony.umbrella.ws.jaxws.JaxWsProxyBuilder;
import com.harmony.umbrella.ws.jaxws.JaxWsServerManager;
import com.harmony.umbrella.ws.ser.UserWebService;

/**
 * @author wuxii@foxmail.com
 */
public class UserProxyTest {

    public static final String address = "http://localhost:8080/user";

    public static EJBContainer container;

    @EJB
    private UserProxy proxy;

    @BeforeClass
    public static void beforeClass() throws Exception {

        Properties props = new Properties();
        props.put("MyJmsResourceAdapter", "new://Resource?type=ActiveMQResourceAdapter");
        props.put("MyJmsResourceAdapter.BrokerXmlConfig", "broker:(tcp://localhost:61616)?useJmx=false");
        props.put("MyJmsResourceAdapter.ServerUrl", "tcp://localhost:61616");

        props.put("MyJmsMdbContainer", "new://Container?type=MESSAGE");
        props.put("MyJmsMdbContainer.ResourceAdapter", "MyJmsResourceAdapter");

        props.put("jms.jaxws.connectionFactory", "new://Resource?type=QueueConnectionFactory");
        props.put("jms.jaxws.connectionFactory.ResourceAdapter", "MyJmsResourceAdapter");

        props.put("jms.jaxws.queue", "new://Resource?type=Queue");

        container = EJBContainer.createEJBContainer(props);

        JaxWsServerManager.getInstance().publish(UserWebService.class, address);

    }

    @Before
    public void before() throws Exception {
        container.getContext().bind("inject", this);
    }

    @Test
    public void testSync() {
        UserService service = JaxWsProxyBuilder.create().build(UserService.class, address);
        service.accessUser("S", new User("wuxii", 18, "F", Calendar.getInstance()));
    }

    @Test
    public void testAsync() throws Exception {
        proxy.sync(new User("wuxii"));
        Thread.sleep(1000 * 2);
        UserService service = JaxWsProxyBuilder.create().build(UserService.class, address);
        assertNotNull(service.findUser("wuxii"));
    }

    public static void main(String[] args) {
        JaxWsServerManager.getInstance().publish(UserWebService.class, address);
    }

}
