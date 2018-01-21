package com.harmony.umbrella.message;

import java.io.Serializable;
import java.util.concurrent.CountDownLatch;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.store.memory.MemoryPersistenceAdapter;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.harmony.umbrella.message.activemq.ActiveMQBrokerServiceBuilder;
import com.harmony.umbrella.message.activemq.ActiveMQMessageTemplateBuilder;
import com.harmony.umbrella.message.annotation.MessageSelector;

/**
 * @author wuxii@foxmail.com
 */
public class MessageTest {

    static String url = "tcp://localhost:61616";

    private static MessageTemplate messageTemplate;
    private static BrokerService brokerService;

    private static DynamicMessageListener textMessageListener;
    private static DynamicMessageListener stringMessageListener;
    private static DynamicMessageListener integerMessageListener;

    private static CountDownLatch listenerConsumeLatch = new CountDownLatch(3);

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
                .setSessionAutoCommit(true)//
                .setMessageMonitor(event -> {
                    System.err.println(event.getEventPhase() + " " + event);
                })//
                .build();

        // ActiveMQ listener 的启动顺序与默认onMessage有关, 如果在没有设置MessageSelector的情况下 就按顺序进行接收消息.
        integerMessageListener = messageTemplate.startMessageListener(new IntegerMessageListener());

        textMessageListener = messageTemplate.startMessageListener(m -> {
            try {
                System.out.println("text message listener consume message: " + ((TextMessage) m).getText());
            } catch (JMSException e) {
            }
            listenerConsumeLatch.countDown();
        });

        stringMessageListener = messageTemplate.startMessageListener(new StringMessageListener());

    }

    @Test
    public void test() throws JMSException, InterruptedException {
        messageTemplate.sendTextMessage("Text Message");
        messageTemplate.sendObjectMessage(new Integer(100));
        messageTemplate.sendObjectMessage("String Message");
        listenerConsumeLatch.await();
    }

    @AfterClass
    public static void afterClass() throws Exception {
        textMessageListener.stop();
        stringMessageListener.stop();
        integerMessageListener.stop();
        brokerService.stop();
    }

    @MessageSelector(type = String.class)
    private static class StringMessageListener implements MessageListener {

        @Override
        public void onMessage(Message message) {
            try {
                Serializable obj = ((ObjectMessage) message).getObject();
                System.out.println("string message listener consume message: " + obj);
            } catch (JMSException e) {
                e.printStackTrace();
            }
            listenerConsumeLatch.countDown();
        }

    }

    @MessageSelector(type = Integer.class)
    private static class IntegerMessageListener implements MessageListener {

        @Override
        public void onMessage(Message message) {
            try {
                Serializable obj = ((ObjectMessage) message).getObject();
                System.out.println("integer message listener consume message: " + obj);
            } catch (JMSException e) {
                e.printStackTrace();
            }
            listenerConsumeLatch.countDown();
        }

    }

}
