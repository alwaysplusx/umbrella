package com.harmony.umbrella.autoconfigure.vertx;

import com.harmony.umbrella.autoconfigure.vertx.VertxAutoConfiguration.ClusterManagerVertxOptionsConfiguration;
import com.harmony.umbrella.autoconfigure.vertx.VertxAutoConfiguration.NoClusterManagerVertxOptionsConfiguration;
import com.harmony.umbrella.autoconfigure.vertx.VertxProperties.DeployOptions;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.ConfigUtil;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Map;
import java.util.Map.Entry;

@Configuration
@ConditionalOnClass({Vertx.class})
@EnableConfigurationProperties(VertxProperties.class)
@Import({NoClusterManagerVertxOptionsConfiguration.class, ClusterManagerVertxOptionsConfiguration.class})
public class VertxAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(VertxAutoConfiguration.class);

    private final VertxProperties vertxProperties;
    private final ApplicationContext applicationContext;

    public VertxAutoConfiguration(VertxProperties vertxProperties, ApplicationContext applicationContext) {
        this.vertxProperties = vertxProperties;
        this.applicationContext = applicationContext;
    }

    @Bean
    @ConditionalOnMissingBean(Vertx.class)
    @ConditionalOnBean(VertxOptions.class)
    public Vertx vertx(VertxOptions vertxOptions) {
        Vertx vertx = Vertx.vertx(vertxOptions);
        if (vertxProperties.isAutoDeploy()) {
            doDeployVerticles(vertx);
        }
        return vertx;
    }

    private void doDeployVerticles(Vertx vertx) {
        Map<String, DeployOptions> deployOptions = vertxProperties.getDeployOptions();
        Map<String, Verticle> verticles = applicationContext.getBeansOfType(Verticle.class);
        DeployOptions defaultDeployOption = deployOptions.getOrDefault("default", new DeployOptions());
        for (Entry<String, Verticle> entry : verticles.entrySet()) {
            DeployOptions deployOption = deployOptions.getOrDefault(entry.getKey(), defaultDeployOption);
            vertx.deployVerticle(entry.getValue(), deployOption, (asyncResult) -> {
                if (asyncResult.succeeded()) {
                    log.info("deploy verticle succeeded, {}@{} ", entry.getKey(), asyncResult.result());
                } else {
                    log.warn("deploy verticle failed. {}", entry.getKey(), asyncResult.cause());
                }
            });
        }
    }

    @Configuration
    @ConditionalOnMissingBean({VertxOptions.class, ClusterManager.class})
    @ConditionalOnProperty(prefix = "harmony.vertx", name = "cluster", havingValue = "false", matchIfMissing = true)
    static class NoClusterManagerVertxOptionsConfiguration {

        @Bean
        public VertxOptions vertxOptions(VertxProperties vertxProperties) {
            return getOrCreateVertxOptions(vertxProperties);
        }

    }

    @Configuration
    @ConditionalOnMissingBean(VertxOptions.class)
    @ConditionalOnClass(HazelcastClusterManager.class)
    @ConditionalOnProperty(prefix = "harmony.vertx", name = "cluster", havingValue = "true", matchIfMissing = true)
    static class ClusterManagerVertxOptionsConfiguration {

        @Bean
        @ConditionalOnMissingBean(ClusterManager.class)
        public ClusterManager clusterManager() {
            HazelcastClusterManager clusterManager = new HazelcastClusterManager();
            clusterManager.setConfig(ConfigUtil.loadConfig());
            return clusterManager;
        }

        @Bean
        public VertxOptions vertxOptions(VertxProperties vertxProperties,
                                         ClusterManager clusterManager) {
            VertxOptions vertxOptions = getOrCreateVertxOptions(vertxProperties);
            return vertxOptions.setClusterManager(clusterManager);
        }

    }

    static VertxOptions getOrCreateVertxOptions(VertxProperties vertxProperties) {
        VertxOptions options = vertxProperties.getOptions();
        if (options == null) {
            options = new VertxOptions();
        }
        return options;
    }

}
