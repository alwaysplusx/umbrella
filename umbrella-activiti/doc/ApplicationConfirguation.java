package com.harmony.umbrella.activiti.config;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;

/**
 * @author wuxii@foxmail.com
 */
@Configuration
@PropertySources({ @PropertySource(value = "classpath:db.properties", ignoreResourceNotFound = true) })
public class ApplicationConfirguation {

    @Autowired
    private Environment env;

    @Bean
    public DataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(env.getProperty("jdbc.driver", "org.h2.Driver"));
        dataSource.setUrl(env.getProperty("jdbc.url", "jdbc:h2:file:~/.h2/umbrella/activiti"));
        dataSource.setUsername(env.getProperty("jdbc.username", "sa"));
        dataSource.setPassword(env.getProperty("jdbc.password", ""));
        return dataSource;
    }

}
