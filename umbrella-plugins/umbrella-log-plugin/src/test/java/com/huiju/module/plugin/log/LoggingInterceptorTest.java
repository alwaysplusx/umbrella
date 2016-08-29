package com.huiju.module.plugin.log;

import javax.ejb.EJB;
import javax.ejb.embeddable.EJBContainer;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author wuxii@foxmail.com
 */
public class LoggingInterceptorTest {

    private static EJBContainer container;

    @EJB
    private TestBean testBean;

    @BeforeClass
    public static void beforeClass() {
        container = EJBContainer.createEJBContainer();
    }

    @Before
    public void setUp() throws Exception {
        container.getContext().bind("inject", this);
    }

    @Test
    public void testPing() {
        testBean.ping(new TestBean("wuxii", 1));
    }

    @Test
    public void test() {
        testBean.test(new TestBean("wuxii", 1));
    }

    @Test
    public void testInternal() {
        testBean.sayHi("wuxii");
    }

}
