package com.harmony.umbrella.autoconfigure.id;

import com.harmony.umbrella.core.IdGenerator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.downgoon.snowflake.Snowflake;

@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass({Snowflake.class})
@EnableConfigurationProperties(SnowflakeProperties.class)
public class SnowflakeAutoConfiguration {

    private final SnowflakeProperties snowflakeProperties;

    public SnowflakeAutoConfiguration(SnowflakeProperties snowflakeProperties) {
        this.snowflakeProperties = snowflakeProperties;
    }

    @Bean
    @ConditionalOnMissingBean(Snowflake.class)
    public IdGenerator<Long> snowflakeIdGenerator() {
        Snowflake snowflake = new Snowflake(snowflakeProperties.getDatacenterId(), snowflakeProperties.getWorkerId());
        return new SnowflakeIdGenerator(snowflake);
    }

}
