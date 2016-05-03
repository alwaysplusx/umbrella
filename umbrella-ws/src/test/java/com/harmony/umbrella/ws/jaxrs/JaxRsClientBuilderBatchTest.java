package com.harmony.umbrella.ws.jaxrs;

import static org.junit.Assert.*;

import com.harmony.umbrella.ws.cxf.interceptor.MessageInInterceptor;
import com.harmony.umbrella.ws.cxf.interceptor.MessageOutInterceptor;
import com.harmony.umbrella.ws.services.HelloRESTService;
import com.harmony.umbrella.ws.services.HelloService;

/**
 * @author wuxii@foxmail.com
 */
public class JaxRsClientBuilderBatchTest {

    private static final String address = "http://localhost:9000/demo/batch";

    public static void main(String[] args) {
        
        JaxRsServerBuilder.create()//
                .setAddress(address)//
                .addInInterceptor(new MessageInInterceptor())//
                .addOutInterceptor(new MessageOutInterceptor())//
                .publish(HelloRESTService.class);

        for (final int index : new int[] { 1, 2, 3, 4, 5 }) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    while (true) {
                        try {
                            HelloService service = JaxRsProxyBuilder.create().build(HelloService.class, address);
                            assertEquals("Hi abc" + index, service.sayHi("abc" + index));
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                        }
                    }
                }

            }).start();
        }

        while (true) {
            try {
                HelloService service = JaxRsProxyBuilder.create().build(HelloService.class, address);
                assertEquals("Hi def", service.sayHi("def"));
                Thread.sleep(400);
            } catch (InterruptedException e) {
            }
        }

    }

}
