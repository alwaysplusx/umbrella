package com.harmony.umbrella.ws.cxf.interceptor;

import javax.xml.ws.Endpoint;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.harmony.umbrella.ws.jaxws.JaxWsProxyBuilder;
import com.harmony.umbrella.ws.services.HelloService;
import com.harmony.umbrella.ws.services.HelloWebService;

/**
 * @author wuxii@foxmail.com
 */
public class BeanInjectInterceptorTest {

    private static final String ADDRESS = "http://localhost:9999/hi";

    @BeforeClass
    public static void beforeClass() {
        // Endpoint.publish(ADDRESS, new HelloWebService());
    }

    public static void main(String[] args) {
        Endpoint.publish(ADDRESS, new HelloWebService());
    }

    @Test
    @Ignore
    public void test() {
        HelloService helloService = JaxWsProxyBuilder.create()//
                .setAddress(ADDRESS)//
                .addInInterceptor(new MessageInInterceptor())//
                .addOutInterceptor(new MessageOutInterceptor())//
                .build(HelloService.class);
        helloService.sayHi("wuxii");
    }
}
