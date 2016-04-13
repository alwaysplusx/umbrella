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
package com.harmony.umbrella.message;

import java.util.Arrays;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import com.harmony.umbrella.config.ConfigSupport;
import com.harmony.umbrella.config.Configurations;
import com.harmony.umbrella.config.annotation.Bean;

/**
 * @author wuxii@foxmail.com
 */
@Stateless(mappedName = Configurations.APPLICATION_CONFIGURATIONS)
@Remote({ Configurations.class })
public class ApplicationConfigurations extends ConfigSupport {

    @EJB(mappedName = "ApplicationMessageMessageResolver")
    private MessageResolver resolver;

    @Bean
    public List<MessageResolver> applicationMessageListenerMessageResolvers() {
        return Arrays.asList(resolver);
    }

}
