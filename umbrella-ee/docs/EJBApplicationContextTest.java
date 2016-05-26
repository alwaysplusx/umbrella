package com.harmony.umbrella.context.ee;

import static org.junit.Assert.*;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.harmony.umbrella.context.bean.InjectBean;
import com.harmony.umbrella.context.bean.JeeSessionRemote;

/**
 * @author wuxii@foxmail.com
 */
public class EJBApplicationContextTest {

	private static EJBContainer container;

	@BeforeClass
	public static void setUp() {
		container = EJBContainer.createEJBContainer();
	}

	@Test
	public void testInject() throws NamingException {
		Context context = container.getContext();
		Object bean = new InjectBean();
		context.bind("java:inject/bean", bean);
		Object lookup = context.lookup("java:inject/bean");
		assertTrue(bean == lookup);
		assertNull(((InjectBean) lookup).bean);
	}

	@Test
	public void testLookupClass() {
		EJBApplicationContext context = EJBApplicationContext.getInstance();
		JeeSessionRemote bean = context.lookup(JeeSessionRemote.class);
		assertNotNull(bean);
	}

	@Test
	public void testLookup() throws Exception {
		Context ctx0 = container.getContext();
		Context ctx1 = (Context) ctx0.lookup("java:");
		Object obj0 = ctx0.lookup("java:/global/umbrella-ee/JeeSessionBean!com.harmony.umbrella.context.bean.JeeSessionRemote");
		Object obj1 = ctx1.lookup("java:/global/umbrella-ee/JeeSessionBean!com.harmony.umbrella.context.bean.JeeSessionRemote");
		assertNotNull(obj0);
		assertNotNull(obj1);
	}

	@AfterClass
	public static void tearDown() {
		container.close();
	}

}
// public void loop(Context context) throws NamingException {
// String namespace = context.getNameInNamespace();
// System.out.println(">> " + namespace);
// NamingEnumeration<Binding> bindings = context.listBindings("");
// while (bindings.hasMoreElements()) {
// Binding binding = (Binding) bindings.nextElement();
// if (binding.getObject() instanceof Context) {
// loop((Context) binding.getObject());
// }
// }
// }
// @Test
// public void testListBindsing() throws Exception {
// NamingEnumeration<Binding> bindings =
// container.getContext().listBindings("java:");
// while (bindings.hasMoreElements()) {
// Binding binding = (Binding) bindings.nextElement();
// System.err.println("class Name = " + binding.getClassName());
// System.err.println("name = " + binding.getName());
// try {
// System.err.println("namespace = " + binding.getNameInNamespace());
// } catch (Exception e) {
// }
// System.err.println("object = " + binding.getObject());
// System.out.println();
// }
// }