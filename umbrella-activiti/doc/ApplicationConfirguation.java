/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
