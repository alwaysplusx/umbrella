package com.harmony.umbrella.message.activemq;

import java.io.File;
import java.io.IOException;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.SslBrokerService;
import org.apache.activemq.store.PersistenceAdapter;
import org.apache.activemq.store.PersistenceAdapterFactory;

/**
 * @author wuxii@foxmail.com
 */
public class ActiveMQBrokerServiceBuilder {

    private final BrokerService brokerService;

    public static ActiveMQBrokerServiceBuilder newBuilder() {
        return newBuilder(false);
    }

    public static ActiveMQBrokerServiceBuilder newBuilder(boolean ssl) {
        return new ActiveMQBrokerServiceBuilder(ssl);
    }

    public ActiveMQBrokerServiceBuilder(boolean ssl) {
        this.brokerService = ssl ? new SslBrokerService() : new BrokerService();
    }

    public ActiveMQBrokerServiceBuilder setConnector(String address) {
        try {
            brokerService.addConnector(address);
        } catch (Exception e) {
            throw new IllegalArgumentException("illegal connector address " + address, e);
        }
        return this;
    }

    public ActiveMQBrokerServiceBuilder setNetworkConnector(String address) {
        try {
            brokerService.addNetworkConnector(address);
        } catch (Exception e) {
            throw new IllegalArgumentException("illegal network connector address " + address, e);
        }
        return this;
    }

    public ActiveMQBrokerServiceBuilder setPersistenceAdapter(PersistenceAdapter persistenceAdapter) {
        try {
            brokerService.setPersistenceAdapter(persistenceAdapter);
        } catch (IOException e) {
            throw new IllegalStateException("unable connection to persistence adapter", e);
        }
        return this;
    }

    public ActiveMQBrokerServiceBuilder setPersistenceAdapterFactroy(PersistenceAdapterFactory persistenceFactory) {
        brokerService.setPersistenceFactory(persistenceFactory);
        return this;
    }

    public ActiveMQBrokerServiceBuilder setPersistent(boolean persistent) {
        brokerService.setPersistent(persistent);
        return this;
    }

    public ActiveMQBrokerServiceBuilder setTmpDataDirectory(String directory) {
        brokerService.setTmpDataDirectory(new File(directory));
        return this;
    }

    public ActiveMQBrokerServiceBuilder setTmpDataDirectory(File directory) {
        brokerService.setTmpDataDirectory(directory);
        return this;
    }

    public ActiveMQBrokerServiceBuilder setDataDirectory(String directory) {
        brokerService.setDataDirectory(directory);
        return this;
    }

    public ActiveMQBrokerServiceBuilder setDataDirectory(File directory) {
        brokerService.setDataDirectoryFile(directory);
        return this;
    }

    // public ActiveMQBrokerServiceBuilder setTrustedPackages(String... trustedPackages) {
    // return null;
    // }

    public BrokerService start() {
        try {
            brokerService.start();
        } catch (Exception e) {
            throw new IllegalStateException("can't start broker service " + brokerService.getBrokerName(), e);
        }
        return brokerService;
    }

}
