/*
 * Copyright 2002-2015 the original author or authors.
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
package com.harmony.umbrella.context;

import com.harmony.umbrella.context.bean.JeeSessionBean;
import com.harmony.umbrella.context.bean.JeeSessionLocal;
import com.harmony.umbrella.context.bean.JeeSessionRemote;
import com.harmony.umbrella.context.ee.BeanDefinition;
import com.harmony.umbrella.context.ee.resolver.ConfigurationBeanResolver;
import com.harmony.umbrella.util.PropUtils;

/**
 * @author wuxii@foxmail.com
 */
public class BeanResolverTest {

    public static void main(String[] args) throws Exception {

        ConfigurationBeanResolver resolver = new ConfigurationBeanResolver(PropUtils.loadProperties("META-INF/application.properties"));

        System.out.println("local :");
        String[] names = resolver.guessNames(new BeanDefinition(JeeSessionLocal.class));
        for (String jndi : names) {
            System.out.println(jndi);
        }
        System.out.println("remote :");
        names = resolver.guessNames(new BeanDefinition(JeeSessionRemote.class));
        for (String jndi : names) {
            System.out.println(jndi);
        }
        System.out.println("bean :");
        names = resolver.guessNames(new BeanDefinition(JeeSessionBean.class));
        for (String jndi : names) {
            System.out.println(jndi);
        }

    }

}
