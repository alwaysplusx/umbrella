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
package com.harmony.modules.jaxws.handler;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.modules.jaxws.JaxWsAbortException;
import com.harmony.modules.jaxws.JaxWsContext;
import com.harmony.modules.jaxws.JaxWsContextHandler;
import com.harmony.modules.jaxws.Phase;
import com.harmony.modules.jaxws.bean.BeanLoader;
import com.harmony.modules.jaxws.bean.ClassBeanLoader;
import com.harmony.modules.jaxws.invoker.DefaultInvoker;
import com.harmony.modules.jaxws.invoker.InvokException;
import com.harmony.modules.jaxws.invoker.Invoker;
import com.harmony.modules.jaxws.util.JaxWsHandlerFinder;

public class JaxWsAnnotationHandler implements JaxWsContextHandler {

    private static final long serialVersionUID = -2065293420059388476L;
    private static Logger log = LoggerFactory.getLogger(JaxWsAnnotationHandler.class);
    private static final String defaultPackage = "com.harmony";
    private Invoker invoker = new DefaultInvoker();
    private BeanLoader beanLoader = new ClassBeanLoader();
    private JaxWsHandlerFinder finder;

    public JaxWsAnnotationHandler() {
        this(defaultPackage);
    }

    public JaxWsAnnotationHandler(String basePackage) {
        this.finder = JaxWsHandlerFinder.newInstance(basePackage);
    }

    @Override
    public boolean preExecute(JaxWsContext context) throws JaxWsAbortException {
        try {
            Method[] methods = finder.findHandlerMethod(context.getMethod(), Phase.PRE_INVOKE);
            for (Method method : methods) {
                Object bean = beanLoader.loadBean(method.getDeclaringClass());
                try {
                    Object result = invoker.invok(bean, method, context.getParameters());
                    if (result instanceof Boolean && !Boolean.valueOf((Boolean) result)) {
                        return false;
                    }
                } catch (InvokException e) {
                    throw new JaxWsAbortException(e);
                }
            }
        } catch (NoSuchMethodException e) {
            throw new JaxWsAbortException(e);
        }
        return true;
    }

    @Override
    public void abortExecute(JaxWsContext context, JaxWsAbortException exception) {
        try {
            Method[] methods = finder.findHandlerMethod(context.getMethod(), Phase.ABORT);
            for (Method method : methods) {
                Object bean = beanLoader.loadBean(method.getDeclaringClass());
                invoker.invok(bean, method, context.getParameters());
            }
        } catch (NoSuchMethodException e) {
            log.error("", e);
        } catch (InvokException e) {
            log.error("", e);
        }
    }

    @Override
    public void postExecute(JaxWsContext context, Object result) {
        try {
            Method[] methods = finder.findHandlerMethod(context.getMethod(), Phase.POST_INVOKE);
            for (Method method : methods) {
                Object bean = beanLoader.loadBean(method.getDeclaringClass());
                invoker.invok(bean, method, context.getParameters());
            }
        } catch (NoSuchMethodException e) {
            log.error("", e);
        } catch (InvokException e) {
            log.error("", e);
        }
    }

    @Override
    public void throwing(JaxWsContext context, Throwable throwable) {
        try {
            Method[] methods = finder.findHandlerMethod(context.getMethod(), Phase.THROWING);
            for (Method method : methods) {
                Object bean = beanLoader.loadBean(method.getDeclaringClass());
                invoker.invok(bean, method, context.getParameters());
            }
        } catch (NoSuchMethodException e) {
            log.error("", e);
        } catch (InvokException e) {
            log.error("", e);
        }
    }

    @Override
    public void finallyExecute(JaxWsContext context, Object result, Exception e) {
        try {
            Method[] methods = finder.findHandlerMethod(context.getMethod(), Phase.FINALLY);
            for (Method method : methods) {
                Object bean = beanLoader.loadBean(method.getDeclaringClass());
                invoker.invok(bean, method, context.getParameters());
            }
        } catch (NoSuchMethodException e1) {
            log.error("", e1);
        } catch (InvokException e1) {
            log.error("", e);
        }
    }

    public void setInvoker(Invoker invoker) {
        this.invoker = invoker;
    }

    public void setBeanLoader(BeanLoader beanLoader) {
        this.beanLoader = beanLoader;
    }

}
