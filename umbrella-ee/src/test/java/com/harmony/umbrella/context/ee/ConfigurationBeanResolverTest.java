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
package com.harmony.umbrella.context.ee;

import java.io.IOException;
import java.util.Properties;

import com.harmony.umbrella.context.ee.resolver.ConfigurationBeanResolver;
import com.harmony.umbrella.util.PropUtils;

/**
 * @author wuxii@foxmail.com
 */
public class ConfigurationBeanResolverTest {

    public static void main(String[] args) throws IOException {
        Properties properties = PropUtils.loadProperties("META-INF/application.properties");
        ConfigurationBeanResolver resolver = ConfigurationBeanResolver.create(properties);
        String[] names = resolver.guessNames(new BeanDefinition(TestRemote.class));
        for (String name : names) {
            System.out.println(name);
        }
    }

}
