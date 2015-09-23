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
package com.harmony.umbrella.ws.cxf.interceptor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ListIterator;

import org.apache.cxf.common.util.ReflectionUtil;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.interceptor.InterceptorChain;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.harmony.umbrella.context.ApplicationContext;
import com.harmony.umbrella.core.BeanFactory;
import com.harmony.umbrella.util.MethodUtils;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public class BeanInjectInterceptor extends AbstractPhaseInterceptor<Message> {

    private static final Logger log = LoggerFactory.getLogger(BeanInjectInterceptor.class);
    private BeanFactory beanFactory;

    public BeanInjectInterceptor() {
        this(Phase.PREPARE_SEND_ENDING);
    }

    public BeanInjectInterceptor(String phase) {
        super(phase);
        this.beanFactory = ApplicationContext.getApplicationContext();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void handleMessage(Message message) throws Fault {
        InterceptorChain chain = message.getInterceptorChain();
        ListIterator<Interceptor<? extends Message>> iterator = chain.getIterator();
        while (iterator.hasNext()) {
            Interceptor<? extends Message> interceptor = iterator.next();
            Class<? extends Interceptor> interceptorClass = interceptor.getClass();
            Field[] fields = ReflectionUtil.getDeclaredFields(interceptorClass);
            for (Field field : fields) {
                Inject ann = field.getAnnotation(Inject.class);
                Object bean = null;
                if (ann != null) {
                    try {
                        if (StringUtils.isNotBlank(ann.value())) {
                            bean = beanFactory.getBean(ann.value());
                        } else {
                            bean = beanFactory.getBean(field.getType());
                        }
                        try {
                            Method method = MethodUtils.findWriterMethod(interceptorClass, field);
                            method.invoke(interceptor, bean);
                        } catch (NoSuchMethodException e) {
                            ReflectionUtil.setAccessible(field);
                            field.set(interceptor, bean);
                        }
                    } catch (Exception e) {
                        log.warn(interceptor + " inject " + field.getType().getName() + " failed", e.toString());
                    }
                }
            }
        }
    }

    @Documented
    @Target({ ElementType.FIELD, ElementType.METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Inject {

        String value() default "";

    }
}
