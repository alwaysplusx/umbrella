package com.harmony.umbrella.test;

import static org.junit.Assert.*;

import javax.ejb.EJB;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author wuxii@foxmail.com
 */
@Ignore
@RunWith(EJBJUnit4ClassRunner.class)
@ContainerConfiguration(location = "classpath:/openejb.properties")
public class OpenEJBTest {

    @EJB
    private SampleRemote sample;

    @Before
    public void setUp() {
        System.out.println("hello");
    }

    @Test
    public void testSayHi() {
        assertNotNull(sample);
        assertEquals("Hi wuxii", sample.sayHi("wuxii"));
    }

}
