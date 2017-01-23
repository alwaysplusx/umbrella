package com.harmony.umbrella.message;

import java.util.Scanner;

import javax.jms.JMSException;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.harmony.umbrella.message.activemq.ActiveMQMessageHelperBuilder;

/**
 * @author wuxii@foxmail.com
 */
@Ignore
public class MessageTest {

    private static MessageHelper messageHelper;

    @BeforeClass
    public static void beforeClass() {
        messageHelper = new ActiveMQMessageHelperBuilder()//
                .connectionFactoryURL("tcp://localhost:61616")//
                .queueName("queue")//
                .build();
    }

    @Test
    public void test() throws JMSException, InterruptedException {
        messageHelper.setListener(m -> {
            System.out.println("receive message " + m);
        });
        messageHelper.sendTextMessage("Hello World!");
        messageHelper.stopListener();
    }

    public static void main(String[] args) throws JMSException {
        beforeClass();
        Scanner scan = new Scanner(System.in);
        messageHelper.setListener(m -> {
            System.err.println("receive message " + m);
        });
        while (true) {
            String text = scan.nextLine();
            if (text.equals("exit")) {
                break;
            }
            messageHelper.sendTextMessage(text);
        }
        messageHelper.stopListener();
        scan.close();
        System.exit(0);
    }

}
