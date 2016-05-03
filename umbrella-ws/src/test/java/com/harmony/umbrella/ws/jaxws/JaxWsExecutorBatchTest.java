package com.harmony.umbrella.ws.jaxws;

import static org.junit.Assert.*;

import com.harmony.umbrella.ws.cxf.interceptor.MessageInInterceptor;
import com.harmony.umbrella.ws.cxf.interceptor.MessageOutInterceptor;
import com.harmony.umbrella.ws.services.HelloService;
import com.harmony.umbrella.ws.services.HelloWebService;
import com.harmony.umbrella.ws.support.SimpleContext;

/**
 * @author wuxii@foxmail.com
 */
public class JaxWsExecutorBatchTest {

    private static final String address = "http://localhost:8081/hello/batch/executor";

    private static JaxWsExecutor executor = new JaxWsCXFExecutor();

    public static void main(String[] args) {
        JaxWsServerBuilder.create()//
                .addInInterceptor(new MessageInInterceptor())//
                .addOutInterceptor(new MessageOutInterceptor())//
                .publish(HelloWebService.class, address);
        test();
    }

    public static void test() {

        for (final int index : new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20 }) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    while (true) {
                        try {
                            SimpleContext context = new SimpleContext(HelloService.class, "sayHi", new Object[] { "abc" + index });
                            context.setAddress(address);
                            assertEquals("Hi abc" + index, executor.execute(context));
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                    System.err.println(">>> assert error thread exit");
                }

            }).start();
        }

        while (true) {
            try {
                SimpleContext context = new SimpleContext(HelloService.class, "sayHi", new Object[] { "wuxii" });
                context.setAddress(address);
                assertEquals("Hi wuxii", executor.execute(context));
                Thread.sleep(400);
            } catch (InterruptedException e) {
            }
        }

    }

}
