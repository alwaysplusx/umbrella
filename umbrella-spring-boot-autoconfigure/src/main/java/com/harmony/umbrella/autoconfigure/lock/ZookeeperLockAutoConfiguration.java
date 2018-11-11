package com.harmony.umbrella.autoconfigure.lock;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.CuratorFrameworkFactory.Builder;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.retry.RetryForever;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.retry.RetryUntilElapsed;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.zookeeper.lock.ZookeeperLockRegistry;
import org.springframework.util.ClassUtils;

import com.harmony.umbrella.autoconfigure.lock.LockProperties.Retry;
import com.harmony.umbrella.autoconfigure.lock.LockProperties.RetryType;
import com.harmony.umbrella.autoconfigure.lock.LockProperties.Zookeeper;

/**
 * @author wuxii@foxmail.com
 */
@Configuration
@EnableConfigurationProperties(LockProperties.class)
@ConditionalOnClass({ CuratorFramework.class, org.apache.zookeeper.ZooKeeper.class })
class ZookeeperLockAutoConfiguration {

    private final LockProperties lockProperties;

    public ZookeeperLockAutoConfiguration(LockProperties lockProperties) {
        this.lockProperties = lockProperties;
    }

    @Bean(destroyMethod = "close")
    public CuratorFramework curatorFramework() throws Exception {
        Zookeeper zk = this.lockProperties.getZookeeper();
        Builder builder = CuratorFrameworkFactory.builder();

        builder.connectString(zk.getAddress())//
                .retryPolicy(retryPolicy());

        if (zk.getConnectionTimeout() != null) {
            builder.connectionTimeoutMs(zk.getConnectionTimeout());
        }

        if (zk.getMaxCloseWait() != null) {
            builder.maxCloseWaitMs(zk.getMaxCloseWait());
        }

        if (zk.getSessionTimeout() != null) {
            builder.sessionTimeoutMs(zk.getSessionTimeout());
        }

        if (zk.getNamespace() != null) {
            builder.namespace(zk.getNamespace());
        }

        CuratorFramework curatorFramework = builder.build();
        if (zk.isAutoStart()) {
            curatorFramework.start();
        }

        return curatorFramework;
    }

    @Bean
    public ZookeeperLockRegistry lockRegistry(CuratorFramework curatorFramework) {
        if (curatorFramework.getState() != CuratorFrameworkState.STARTED) {
            curatorFramework.start();
        }
        return new ZookeeperLockRegistry(curatorFramework);
    }

    private RetryPolicy retryPolicy() {
        RetryPolicy retryPolicy = null;
        Retry retry = lockProperties.getZookeeper().getRetry();
        try {
            Class<?> retryType = ClassUtils.forName(retry.getType(), null);
            retryPolicy = (RetryPolicy) BeanUtils.instantiateClass(retryType);
        } catch (ClassNotFoundException e) {
            RetryType type = RetryType.forName(retry.getType());
            switch (type) {
            case Forever:
                retryPolicy = new RetryForever(retry.getInterval());
                break;
            case NTimes:
                retryPolicy = new RetryNTimes(retry.getTimes(), retry.getInterval());
                break;
            case UntilElapsed:
                retryPolicy = new RetryUntilElapsed(retry.getMaxElapsedTime(), retry.getInterval());
                break;
            }
        }
        return retryPolicy;
    }

}
