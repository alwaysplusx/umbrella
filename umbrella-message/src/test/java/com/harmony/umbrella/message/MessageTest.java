package com.harmony.umbrella.message;

import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.store.memory.MemoryPersistenceAdapter;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.harmony.umbrella.message.activemq.ActiveMQBrokerServiceBuilder;
import com.harmony.umbrella.message.activemq.ActiveMQMessageTemplateBuilder;

/**
 * @author wuxii@foxmail.com
 */
public class MessageTest {

    static String url = "tcp://localhost:61616";

    private static MessageTemplate messageTemplate;
    private static BrokerService brokerService;

    @BeforeClass
    public static void beforeClass() {
        brokerService = ActiveMQBrokerServiceBuilder//
                .newBuilder()//
                .setPersistenceAdapter(new MemoryPersistenceAdapter())//
                .setTmpDataDirectory("target/activemq")//
                .setConnector(url)//
                .start();
        messageTemplate = new ActiveMQMessageTemplateBuilder()//
                .setConnectionFactoryURL(url)//
                .setQueueName("queue")//
                .setMessageMonitor(event -> {
                    System.err.println(event);
                })//
                .build();
    }

    @Test
    public void test() throws JMSException, InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        messageTemplate.setMessageListener(m -> {
            try {
                System.err.println("receive message " + ((TextMessage) m).getText());
            } catch (JMSException e) {
            }
            latch.countDown();
        });
        messageTemplate.sendTextMessage("Hello World!");
        latch.await();
        messageTemplate.stopMessageListener();
    }

    @AfterClass
    public static void afterClass() throws Exception {
        brokerService.stop();
    }

    public static void main(String[] args) throws JMSException {
        beforeClass();
        Scanner scan = new Scanner(System.in);
        messageTemplate.setMessageListener(m -> {
            System.err.println("receive message " + m);
        });
        while (true) {
            String text = scan.nextLine();
            if (text.equals("exit")) {
                break;
            }
            messageTemplate.sendTextMessage(text);
        }
        messageTemplate.stopMessageListener();
        scan.close();
        System.exit(0);
    }

}
