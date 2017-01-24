package com.harmony.umbrella.message.activemq;

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

    public static ActiveMQBrokerServiceBuilder createBuilder() {
        return createBuilder(false);
    }

    public static ActiveMQBrokerServiceBuilder createBuilder(boolean ssl) {
        return new ActiveMQBrokerServiceBuilder(ssl);
    }

    public ActiveMQBrokerServiceBuilder(boolean ssl) {
        this.brokerService = ssl ? new SslBrokerService() : new BrokerService();
    }

    public ActiveMQBrokerServiceBuilder connector(String address) {
        try {
            brokerService.addConnector(address);
        } catch (Exception e) {
            throw new IllegalArgumentException("illegal connector address " + address, e);
        }
        return this;
    }

    public ActiveMQBrokerServiceBuilder networkConnector(String address) {
        try {
            brokerService.addNetworkConnector(address);
        } catch (Exception e) {
            throw new IllegalArgumentException("illegal network connector address " + address, e);
        }
        return this;
    }

    public ActiveMQBrokerServiceBuilder persistenceAdapter(PersistenceAdapter persistenceAdapter) {
        try {
            brokerService.setPersistenceAdapter(persistenceAdapter);
        } catch (IOException e) {
            throw new IllegalStateException("unable connection to persistence adapter", e);
        }
        return this;
    }

    public ActiveMQBrokerServiceBuilder persistenceAdapterFactroy(PersistenceAdapterFactory persistenceFactory) {
        brokerService.setPersistenceFactory(persistenceFactory);
        return this;
    }

    public ActiveMQBrokerServiceBuilder persistent(boolean persistent) {
        brokerService.setPersistent(persistent);
        return this;
    }

    public BrokerService getBrokerService() {
        return brokerService;
    }

    public BrokerService getBrokerService(boolean start) {
        if (start) {
            try {
                brokerService.start();
            } catch (Exception e) {
                throw new IllegalStateException("can't start broker service " + brokerService.getBrokerName(), e);
            }
        }
        return brokerService;
    }

}
