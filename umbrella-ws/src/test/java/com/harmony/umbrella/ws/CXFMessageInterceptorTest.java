/*
 * Copyright 2013-2015 wuxii@foxmail.com.
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
package com.harmony.umbrella.ws;

import java.util.Set;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

import com.harmony.umbrella.io.Resource;
import com.harmony.umbrella.io.ResourceManager;
import com.harmony.umbrella.ws.jaxws.JaxWsProxyBuilder;
import com.harmony.umbrella.ws.jaxws.JaxWsServerBuilder;
import com.harmony.umbrella.ws.services.HelloService;
import com.harmony.umbrella.ws.services.HelloWebService;

/**
 * @author wuxii@foxmail.com
 */
public class CXFMessageInterceptorTest {

    private static final String address = "http://localhost:8081/hello";

    public static void main(String[] args) {
        JaxWsServerBuilder.create().publish(HelloWebService.class, address, new FactoryConfig<JaxWsServerFactoryBean>() {

            @Override
            public void config(JaxWsServerFactoryBean factoryBean) {
                factoryBean.getInInterceptors().add(new AbstractPhaseInterceptor<Message>(Phase.RECEIVE) {

                    @Override
                    public void handleMessage(Message message) throws Fault {
                        
                        Set<String> keySet = message.keySet();
                        for (String string : keySet) {
                            Object obj = message.get(string);
                            String type = obj != null ? obj.getClass().getName() : "null";
                            System.out.println("server message: key=" + string + ", value=" + obj + ", type=" + type);
                        }
                        
                        System.out.println();
                        Set<Class<?>> contentFormats = message.getContentFormats();
                        for (Class<?> class1 : contentFormats) {
                            Object obj = message.getContent(class1);
                            String type = obj != null ? obj.getClass().getName() : "null";
                            System.out.println("server content: key=" + class1.getName() + ", value=" + obj + ", type=" + type);
                        }
                        
                        System.out.println();
                        Set<String> contextualPropertyKeys = message.getContextualPropertyKeys();
                        for (String string : contextualPropertyKeys) {
                            Object obj = message.getContextualProperty(string);
                            String type = obj != null ? obj.getClass().getName() : "null";
                            System.out.println("server contextual: key=" + string + ", value=" + obj + ", type=" + type);
                        }
                    }

                    @Override
                    public void handleFault(Message message) {
                    }

                });
            }

        });
        JaxWsProxyBuilder builder = JaxWsProxyBuilder.create();
        builder.getInInterceptors().add(new AbstractPhaseInterceptor<Message>(Phase.RECEIVE) {

            @Override
            public void handleMessage(Message message) throws Fault {
                Set<String> keySet = message.keySet();
                for (String string : keySet) {
                    Object obj = message.get(string);
                    String type = obj != null ? obj.getClass().getName() : "null";
                    System.out.println("proxy message: key=" + string + ", value=" + obj + ", type=" + type);
                }
                
                System.out.println();
                Set<Class<?>> contentFormats = message.getContentFormats();
                for (Class<?> class1 : contentFormats) {
                    Object obj = message.getContent(class1);
                    String type = obj != null ? obj.getClass().getName() : "null";
                    System.out.println("proxy content: key=" + class1.getName() + ", value=" + obj + ", type=" + type);
                }
                
                System.out.println();
                Set<String> contextualPropertyKeys = message.getContextualPropertyKeys();
                for (String string : contextualPropertyKeys) {
                    Object obj = message.getContextualProperty(string);
                    String type = obj != null ? obj.getClass().getName() : "null";
                    System.out.println("proxy contextual: key=" + string + ", value=" + obj + ", type=" + type);
                }
            }

        });

        HelloService service = builder.build(HelloService.class, address);

        service.sayHi("wuxii");

        System.exit(0);

    }

    // @Test
    public void testGetResources() {
        Resource[] resources = ResourceManager.getInstance().getResources("com.harmony");
        for (Resource resource : resources) {
            System.out.println(resource.toString());
        }
    }
}
