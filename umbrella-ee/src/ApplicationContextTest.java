package com.harmony.umbrella.context;

import static org.junit.Assert.*;

import java.util.StringTokenizer;

import org.junit.Test;

import com.harmony.umbrella.context.ee.EJBApplicationContext;

/**
 * @author wuxii@foxmail.com
 */
public class ApplicationContextTest {

	@Test
	public void testGetApplicationContext() {
		ApplicationContext context = ApplicationContext.getApplicationContext();
		assertEquals(context.getClass(), EJBApplicationContext.class);
	}

	public static void main(String[] args) {
		StringTokenizer token = new StringTokenizer(",java:,,", ",");
		for (; token.hasMoreElements();) {
			Object nextElement = token.nextElement();
			System.out.println(">>>>" + nextElement);
		}
	}
}
