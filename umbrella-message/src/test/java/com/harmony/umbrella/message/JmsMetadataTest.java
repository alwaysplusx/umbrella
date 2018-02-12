package com.harmony.umbrella.message;

import java.io.PrintStream;
import java.util.Enumeration;

import javax.jms.Connection;
import javax.jms.ConnectionMetaData;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.store.memory.MemoryPersistenceAdapter;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.harmony.umbrella.message.activemq.ActiveMQBrokerServiceBuilder;

/**
 * @author wuxii@foxmail.com
 */
public class JmsMetadataTest {

    static String url = "tcp://localhost:61616";

    private static BrokerService brokerService;

    @BeforeClass
    public static void beforeClass() {
        // start activemq server
        brokerService = ActiveMQBrokerServiceBuilder//
                .newBuilder()//
                .setPersistenceAdapter(new MemoryPersistenceAdapter())//
                .setTmpDataDirectory("target/activemq")//
                .setBrokerUrl(url)//
                .start();
    }

    @Test
    public void test() throws Exception {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        Connection connection = connectionFactory.createConnection();
        ConnectionMetaData metadata = connection.getMetaData();
        PrintStream o = System.out;
        o.println("jms version: " + metadata.getJMSVersion());
        o.println("jms major version: " + metadata.getJMSMajorVersion());
        o.println("jms minor version: " + metadata.getJMSMinorVersion());

        o.println("jms provider name: " + metadata.getJMSProviderName());
        o.println("jms provider version: " + metadata.getProviderVersion());
        o.println("jms provider major version: " + metadata.getProviderMajorVersion());
        o.println("jms provider minor version: " + metadata.getProviderMinorVersion());

        Enumeration names = metadata.getJMSXPropertyNames();
        while (names.hasMoreElements()) {
            o.println("x property name: " + names.nextElement());
        }
        connection.close();
    }

    @AfterClass
    public static void afterClass() throws Exception {
        brokerService.stop();
    }

}
