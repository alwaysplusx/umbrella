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

import java.util.Properties;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.InitialContext;

import org.junit.BeforeClass;
import org.junit.Test;

import com.harmony.umbrella.context.bean.JeeSessionRemote;
import com.harmony.umbrella.context.ee.BeanDefinition;
import com.harmony.umbrella.context.ee.SessionBean;
import com.harmony.umbrella.context.ee.resolver.InternalContextResolver;
import com.harmony.umbrella.util.PropUtils;

/**
 * @author wuxii@foxmail.com
 */
public class ContextResolverTest {

    public static EJBContainer container;

    @BeforeClass
    public static void beforeClass() throws Exception {
        container = EJBContainer.createEJBContainer();
    }

    @Test
    public void testContextResolver() throws Exception {
        Properties props = PropUtils.loadProperties("META-INF/application.properties");
        InternalContextResolver contextResolver = new InternalContextResolver(props);
        InitialContext context = new InitialContext(props);
        BeanDefinition bd = new BeanDefinition(JeeSessionRemote.class);
        String[] names = contextResolver.guessNames(bd);
        for (String name : names) {
            System.out.println(name);
        }

        SessionBean bean = contextResolver.search(bd, context);
        System.out.println(bean);
    }

    /*public static void main(String[] args) throws Exception {
        String jndis = " ,java";
        String[] names = jndis.split(",");
        for (String name : names) {
            System.out.println(name);
        }
        StringTokenizer st = new StringTokenizer(jndis, ",");
        while (st.hasMoreTokens()) {
            System.out.println(st.nextToken());
        }
        Properties props = PropUtils.loadProperties("META-INF/application.properties");
        String roots = props.getProperty("jndi.context.root");
        StringTokenizer st = new StringTokenizer(roots, ",");
        while (st.hasMoreTokens()) {
            System.out.println(" >>>>|" + st.nextToken() + "|");
        }
    }*/
}
