package com.harmony.umbrella.log;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.embeddable.EJBContainer;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.harmony.umbrella.log.annotation.Logging;

/**
 * @author wuxii@foxmail.com
 */
public class LoggingInterceptorTest {

    private static EJBContainer container;

    @EJB
    private LoggerBean bean;

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
        bean.sayHi("wuxii");
    }

    @AfterClass
    public static void afterClass() {
        container.close();
    }

    @Stateless
    public static class LoggerBean {

        @Logging(message = "say hi to user {args[0]}")
        public String sayHi(String name) {
            return "Hi " + name;
        }

    }

}
