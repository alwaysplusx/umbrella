package com.harmony.umbrella.test;

import static org.junit.Assert.*;

import java.util.Properties;

import javax.ejb.EJB;
import javax.ejb.embeddable.EJBContainer;

import org.apache.openejb.OpenEjbContainer.Provider;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author wuxii@foxmail.com
 */
@Ignore
public class SampleTest {

    // org.glassfish.ejb.embedded.EJBContainerProviderImpl
    // org.apache.openejb.OpenEjbContainer$Provider
    public static EJBContainer container;

    @EJB
    private SampleRemote sample;

    @BeforeClass
    public static void beforeClass() {
        Properties props = new Properties();
        props.put("openejb.embedded.remotable", "true");
        container = new Provider().createEJBContainer(props);
    }

    @Before
    public void setUp() throws Exception {
        container.getContext().bind("inject", this);
    }

    @Test
    public void testSayHi() throws Exception {
        assertEquals("Hi wuxii", sample.sayHi("wuxii"));
    }

    public static void main(String[] args) throws Exception {
        SampleTest bean = new SampleTest();
        beforeClass();
        container.getContext().bind("inject", bean);
        Thread.sleep(Long.MAX_VALUE);
    }

}
