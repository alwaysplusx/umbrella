package com.harmony.umbrella.context.ee;

import static org.junit.Assert.*;

import javax.ejb.embeddable.EJBContainer;

import org.junit.BeforeClass;
import org.junit.Test;

import com.harmony.umbrella.context.ee.EJBApplicationContext;

/**
 * @author wuxii@foxmail.com
 */
public class EJBBeanFactoryTest {

    public static EJBContainer container;

    private static EJBApplicationContext context;

    @BeforeClass
    public static void beforeClass() throws Exception {
        container = EJBContainer.createEJBContainer();
        // context = EJBApplicationContext.create(null);
    }

    @Test
    public void testContextResolver() throws Exception {
        assertNotNull(context.getBean("java:global/umbrella-ee/TestBean"));
        assertNotNull(context.getBean(TestRemote.class));
    }

    public static void main(String[] args) throws Exception {
        beforeClass();
        Thread.sleep(Long.MAX_VALUE);
    }

}
