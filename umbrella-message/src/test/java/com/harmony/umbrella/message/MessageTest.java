package com.harmony.umbrella.message;

import java.util.Scanner;

import javax.jms.JMSException;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.harmony.umbrella.message.activemq.ActiveMQMessageTemplateBuilder;

/**
 * @author wuxii@foxmail.com
 */
@Ignore
public class MessageTest {

    private static MessageTemplate messageTemplate;

    @BeforeClass
    public static void beforeClass() {
        messageTemplate = new ActiveMQMessageTemplateBuilder()//
                .connectionFactoryURL("tcp://localhost:61616")//
                .queueName("queue")//
                .build();
    }

    @Test
    public void test() throws JMSException, InterruptedException {
        messageTemplate.setMessageListener(m -> {
            System.out.println("receive message " + m);
        });
        messageTemplate.sendTextMessage("Hello World!");
        messageTemplate.stopMessageListener();
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
