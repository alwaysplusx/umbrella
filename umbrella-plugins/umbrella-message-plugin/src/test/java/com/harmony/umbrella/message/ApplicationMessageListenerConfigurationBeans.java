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

import com.harmony.umbrella.config.ConfigurationBeans;

/**
 * @author wuxii@foxmail.com
 */
@Stateless
@Remote(ConfigurationBeans.class)
public class ApplicationMessageListenerConfigurationBeans implements ConfigurationBeans<MessageResolver> {

    @EJB(mappedName = "ApplicationMessageMessageResolver")
    private MessageResolver resolver;

    @Override
    public List<MessageResolver> getBeans() {
        return Arrays.asList(resolver);
    }

}
