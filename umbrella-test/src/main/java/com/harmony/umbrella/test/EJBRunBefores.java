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
package com.harmony.umbrella.test;

import com.harmony.umbrella.context.ee.EJBBeanFactory;
import com.harmony.umbrella.core.BeansException;
import com.harmony.umbrella.core.NoSuchBeanFoundException;
import com.harmony.umbrella.util.ReflectionUtils;
import com.harmony.umbrella.util.StringUtils;
import org.junit.runners.model.FrameworkField;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;

import javax.ejb.EJB;
import java.lang.reflect.Field;
import java.util.List;

/**
 * @author wuxii@foxmail.com
 */
public class EJBRunBefores extends Statement {

    private Statement next;
    private Object testInstance;
    private TestClass testClass;
    private EJBBeanFactory beanFactory;

    public EJBRunBefores(Statement next, Object testInstance, TestClass testClass, EJBBeanFactory beanFactory) {
        this.next = next;
        this.testInstance = testInstance;
        this.testClass = testClass;
        this.beanFactory = beanFactory;
    }

    @Override
    public void evaluate() throws Throwable {
        List<FrameworkField> fields = testClass.getAnnotatedFields(EJB.class);
        for (FrameworkField field : fields) {
            if (field.get(testInstance) != null) {
                continue;
            }
            Field realField = field.getField();
            Object bean = null;
            String jndi = getJndi(realField);
            if (StringUtils.isNotBlank(jndi)) {
                try {
                    bean = beanFactory.lookup(jndi);
                } catch (BeansException e) {
                    EJB ejbAnnotation = realField.getAnnotation(EJB.class);
                    if (ejbAnnotation != null) {
                        bean = beanFactory.lookup(field.getType(), ejbAnnotation);
                    } else {
                        bean = beanFactory.lookup(field.getType());
                    }
                }
            }

            if (bean == null) {
                throw new NoSuchBeanFoundException(field.getName());
            }

            ReflectionUtils.setFieldValue(realField, testInstance, bean);
        }
        this.next.evaluate();
    }

    private String getJndi(Field field) {
        EJB ann = field.getAnnotation(EJB.class);
        if (StringUtils.isNotBlank(ann.lookup())) {
            return ann.lookup();
        }
        return null;
    }

}
