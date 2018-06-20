package com.harmony.umbrella.autoconfigure.lock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.zookeeper.config.CuratorFrameworkFactoryBean;
import org.springframework.integration.zookeeper.lock.ZookeeperLockRegistry;

/**
 * @author wuxii@foxmail.com
 */
@Configuration
@ConditionalOnClass({ CuratorFramework.class, ZooKeeper.class })
class ZookeeperLockAutoConfiguration {

    // FIXME build CuratorFramework factory bean
    
    @Bean
    public CuratorFrameworkFactoryBean curatorFrameworkFactoryBean() {
        return null;
    }

    @Bean
    public CuratorFramework curatorFramework(CuratorFrameworkFactoryBean factoryBean) throws Exception {
        return factoryBean.getObject();
    }

    @Bean
    public ZookeeperLockRegistry lockRegistry(CuratorFramework curatorFramework) {
        return new ZookeeperLockRegistry(curatorFramework);
    }

}
