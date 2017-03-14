package com.harmony.umbrella.context;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author wuxii@foxmail.com
 */
public class JeeContextTest {

	private static EJBContainer container;

	@BeforeClass
	public static void setUp() {
		container = EJBContainer.createEJBContainer();
	}

	@Test
	public void test() throws Exception {
		Context context = container.getContext();
		Context rootContext = (Context) context.lookup("java:");
		NamingEnumeration<NameClassPair> ncps = rootContext.list("");
		while (ncps.hasMoreElements()) {
			NameClassPair ncp = ncps.next();
			String className = ncp.getClassName();
			String name = ncp.getName();
			String nameInNamespace = "";// ncp.getNameInNamespace();
			System.err.println("className=" + className + ", name=" + name + ", nameInNamespace=" + nameInNamespace);
		}
	}

}
