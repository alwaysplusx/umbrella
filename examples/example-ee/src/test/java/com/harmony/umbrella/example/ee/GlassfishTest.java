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
