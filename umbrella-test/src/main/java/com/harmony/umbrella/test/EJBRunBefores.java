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

import java.lang.reflect.Field;
import java.util.List;

import javax.ejb.EJB;

import com.harmony.umbrella.core.BeansException;
import org.junit.runners.model.FrameworkField;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;

import com.harmony.umbrella.context.ee.EJBApplicationContext;
import com.harmony.umbrella.util.ReflectionUtils;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public class EJBRunBefores extends Statement {

    private Statement next;
    private Object testInstance;
    private TestClass testClass;
    private EJBApplicationContext context;

    public EJBRunBefores(Statement next, Object testInstance, TestClass testClass, EJBApplicationContext context) {
        this.next = next;
        this.testInstance = testInstance;
        this.testClass = testClass;
        this.context = context;
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
            String jndi = getJndi(field);
            if (StringUtils.isNotBlank(jndi)) {
                try {
                    bean = context.lookup(jndi);
                } catch (BeansException e) {
                    EJB ejbAnnotation = realField.getAnnotation(EJB.class);
                    if (ejbAnnotation != null) {
                        try {
                            bean = context.lookup(realField.getType(), ejbAnnotation);
                        } catch (BeansException e1) {
                            bean = context.lookup(realField.getType());
                        }
                    }
                }
            }
            ReflectionUtils.setFieldValue(realField, testInstance, bean);
        }
        this.next.evaluate();
    }

    private String getJndi(FrameworkField field) {
        Field realField = field.getField();
        EJB ann = realField.getAnnotation(EJB.class);
        if (StringUtils.isNotBlank(ann.lookup())) {
            return ann.lookup();
        }
        return null;
    }

}
