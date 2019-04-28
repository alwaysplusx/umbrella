package com.harmony.umbrella.autoconfigure.lock;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryForever;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.retry.RetryUntilElapsed;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.integration.zookeeper.lock.ZookeeperLockRegistry;
import org.springframework.util.ClassUtils;

/**
 * @author wuxii@foxmail.com
 */
@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
@ConditionalOnClass(LockRegistry.class)
@EnableConfigurationProperties(LockProperties.class)
public class LockAutoConfiguration {

    @Configuration
    @ConditionalOnBean(RedisConnectionFactory.class)
    @ConditionalOnMissingBean(LockRegistry.class)
    @Import({LockConfiguration.Redis.class, LockConfiguration.Zookeeper.class})
    static class LockConfiguration {

        @ConditionalOnMissingBean(LockRegistry.class)
        @ConditionalOnClass({RedisConnectionFactory.class, RedisLockRegistry.class})
        @ConditionalOnProperty(name = "harmony.lock.type", havingValue = "redis", matchIfMissing = true)
        static class Redis {

            private final LockProperties lockProperties;

            public Redis(LockProperties lockProperties) {
                this.lockProperties = lockProperties;
            }

            @Bean
            LockRegistry lockRegistry(RedisConnectionFactory redisConnectionFactory) {
                LockProperties.Redis redis = lockProperties.getRedis();
                String registryKey = redis.getRegistryKey();
                long expireAfter = redis.getExpireAfter().toMillis();
                return new RedisLockRegistry(redisConnectionFactory, registryKey, expireAfter);
            }

        }

        @ConditionalOnMissingBean(LockRegistry.class)
        @ConditionalOnClass({CuratorFramework.class, org.apache.zookeeper.ZooKeeper.class})
        @ConditionalOnProperty(name = "harmony.lock.type", havingValue = "zookeeper", matchIfMissing = true)
        static class Zookeeper {

            private final LockProperties lockProperties;

            public Zookeeper(LockProperties lockProperties) {
                this.lockProperties = lockProperties;
            }

            @Bean
            public ZookeeperLockRegistry lockRegistry() {
                return new ZookeeperLockRegistry(curatorFramework());
            }

            public CuratorFramework curatorFramework() {
                LockProperties.Zookeeper zk = this.lockProperties.getZookeeper();
                CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder();

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
                curatorFramework.start();
                return curatorFramework;
            }


            private RetryPolicy retryPolicy() {
                RetryPolicy retryPolicy = null;
                LockProperties.Retry retry = lockProperties.getZookeeper().getRetry();
                try {
                    Class<?> retryType = ClassUtils.forName(retry.getType(), null);
                    retryPolicy = (RetryPolicy) BeanUtils.instantiateClass(retryType);
                } catch (ClassNotFoundException e) {
                    LockProperties.RetryType type = LockProperties.RetryType.forName(retry.getType());
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
    }

}
