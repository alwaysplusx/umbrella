package com.harmony.umbrella.message.web;

import org.apache.activemq.broker.BrokerService;

import com.harmony.umbrella.context.ApplicationConfiguration;
import com.harmony.umbrella.context.listener.ApplicationListener;

/**
 * @author wuxii@foxmail.com
 */
public class ActiveMQBootstrap implements ApplicationListener {

    private BrokerService brokerService;

    @Override
    public void onStartup(ApplicationConfiguration appConfig) {
        appConfig.getStringProperty("activemq.connector");
        try {
            brokerService.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy(ApplicationConfiguration appConfig) {
        try {
            brokerService.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
