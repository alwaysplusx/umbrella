package com.harmony.umbrella.monitor;

import static org.junit.Assert.*;

import java.util.Properties;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.embeddable.EJBContainer;
import javax.interceptor.Interceptor;
import javax.interceptor.Interceptors;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.harmony.umbrella.monitor.support.AbstractEJBMonitorInterceptor;
import com.harmony.umbrella.monitor.support.InvocationContext;

/**
 * @author wuxii@foxmail.com
 */
public class EJBMonitorInterceptorTest {

    private static EJBContainer container;

    @EJB
    private MonitorBean bean;

    private static int check = 0;

    @BeforeClass
    public static void beforeClass() {
        Properties props = new Properties();
        container = EJBContainer.createEJBContainer(props);
    }

    @Before
    public void setUp() throws Exception {
        container.getContext().bind("inject", this);
    }

    @Test
    public void testInterceptor() {
        assertEquals(0, check++);
        bean.monitor();
        assertEquals(3, check);
    }

    @Interceptor
    public static class EJBMonitorInterceptor extends AbstractEJBMonitorInterceptor {
        @Override
        protected Object doInterceptor(InvocationContext invocationContext) throws Exception {
            assertEquals(1, check++);
            return invocationContext.process();
        }

    }

    @Stateless(mappedName = "MonitorBean")
    public static class MonitorBean {
        @Interceptors(EJBMonitorInterceptor.class)
        public void monitor() {
            assertEquals(2, check++);
        }
    }
}
