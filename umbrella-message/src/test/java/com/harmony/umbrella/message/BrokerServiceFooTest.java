package com.harmony.umbrella.message;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author wuxii@foxmail.com
 */
public class BrokerServiceFooTest {

    public static void main(String[] args) throws InterruptedException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:/activemq-foo.xml");
        Thread.sleep(Long.MAX_VALUE);
        context.close();
    }

}
