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
package com.harmony.umbrella.context;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wuxii@foxmail.com
 */
public class NamingContextTest {

    private static final Logger log = LoggerFactory.getLogger(NamingContextTest.class);
    public static EJBContainer container;
    private static Context ctx;

    @BeforeClass
    public static void beforeClass() throws Exception {
        container = EJBContainer.createEJBContainer();
        ctx = new InitialContext();
    }

    @Test
    public void testContext() throws Exception {
        iterator("java:", ctx);
    }

    private void iterator(String root, Context context) throws Exception {
        final String originalRoot = root;
        log.info("iterator context {}", root);
        NamingEnumeration<NameClassPair> ncps = context.list(new CompositeName(root));
        while (ncps.hasMoreElements()) {
            NameClassPair ncp = ncps.next();
            root = root + "/" + ncp.getName();
            Object obj;
            try {
                obj = context.lookup(root);
                if (obj instanceof Context) {
                    iterator(root, (Context) obj);
                } else {
                    log.info("find not context bean {}, in context [{}]", obj, root);
                }
            } catch (Exception e) {
                log.error("context [{}] not find", root);
            }
        }
        log.info("find all context in root {}", originalRoot);
    }

    public static void main(String[] args) throws Exception {
        /*NamingContextTest ct = new NamingContextTest();
        NamingContextTest.beforeClass();
        ct.iterator("java:", ctx);*/
        CompositeName name = new CompositeName("java:/comp/evn");
        Name prefixName = name.getPrefix(1);
        System.out.println(name);
        System.out.println(prefixName);
    }

}
