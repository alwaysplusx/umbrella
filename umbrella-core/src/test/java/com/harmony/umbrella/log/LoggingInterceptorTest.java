package com.harmony.umbrella.log;

import static org.junit.Assert.*;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.ejb.embeddable.EJBContainer;
import javax.interceptor.Interceptors;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.harmony.umbrella.log.annotation.Logging;
import com.harmony.umbrella.log.interceptor.LoggingInterceptor;

/**
 * @author wuxii@foxmail.com
 */
public class LoggingInterceptorTest {

    private static EJBContainer container;

    @EJB
    private LoggerRemote bean;

    @BeforeClass
    public static void beforeClass() {
        container = EJBContainer.createEJBContainer();
    }

    @Before
    public void setUp() throws Exception {
        container.getContext().bind("inject", this);
    }

    @Test
    public void test() {
        String result = bean.sayHi("wuxii");
        assertEquals("Hi wuxii", result);
    }

    @AfterClass
    public static void afterClass() {
        container.close();
    }

    @Remote
    public interface LoggerRemote {

        String sayHi(String name);

    }

    @Stateless
    public static class LoggerBean implements LoggerRemote {

        @Interceptors(LoggingInterceptor.class)
        @Logging(message = "say hi to user {args[0]}")
        public String sayHi(String name) {
            return "Hi " + name;
        }

    }

}
