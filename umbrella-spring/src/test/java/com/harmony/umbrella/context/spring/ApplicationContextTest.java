package com.harmony.umbrella.context.spring;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

import com.harmony.umbrella.context.ApplicationContext;

/**
 * @author wuxii@foxmail.com
 */
@Ignore
public class ApplicationContextTest {

    @Test
    public void testGetApplicationContext() {
        ApplicationContext context = ApplicationContext.getApplicationContext();
        assertEquals(context.getClass(), SpringApplicationContext.class);
    }

    public static void main(String[] args) {
        ApplicationContext context = ApplicationContext.getApplicationContext();
        System.out.println(context);
    }
}
