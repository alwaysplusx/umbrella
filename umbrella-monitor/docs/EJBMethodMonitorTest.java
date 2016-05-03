package com.harmony.umbrella.monitor;

import java.util.Properties;

import javax.ejb.EJB;
import javax.ejb.embeddable.EJBContainer;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author wuxii@foxmail.com
 */
public class EJBMethodMonitorTest {

    public static EJBContainer container;

    @EJB
    private HiBean hiBean;

    @BeforeClass
    public static void beforeClass() throws Exception {
        Properties props = new Properties();
        container = EJBContainer.createEJBContainer(props);
    }

    @Before
    public void before() throws Exception {
        container.getContext().bind("inject", this);
    }

    @Test
    public void testMonitor() {
        hiBean.sayHi("wuxii");
    }

}
