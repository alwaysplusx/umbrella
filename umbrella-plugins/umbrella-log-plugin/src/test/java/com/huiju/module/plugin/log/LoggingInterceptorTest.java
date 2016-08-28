package com.huiju.module.plugin.log;

import javax.ejb.EJB;
import javax.ejb.embeddable.EJBContainer;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeFilter;

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

    public static void main(String[] args) {
        A a = new A();
        B b = new B();
        a.b = b;
        b.a = a;
        String text = JSON.toJSONString(a, new SerializeFilter[0]);
        Object object = JSON.parse(text);
        System.out.println(object);
    }

    public static class A {
        private B b;

        public A() {
        }

        public A(B b) {
            this.b = b;
        }

        public B getB() {
            return b;
        }

        public void setB(B b) {
            this.b = b;
        }
    }

    public static class B {
        private A a;

        public B() {
        }

        public B(A a) {
            this.a = a;
        }

        public A getA() {
            return a;
        }

        public void setA(A a) {
            this.a = a;
        }

    }

}
