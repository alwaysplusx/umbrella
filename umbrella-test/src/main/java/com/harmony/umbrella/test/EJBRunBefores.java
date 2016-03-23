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
            Field realField = field.getField();
            if (ReflectionUtils.getFieldValue(realField, testInstance) != null) {
                continue;
            }
            String jndi = getJndi(field);
            if (StringUtils.isNotBlank(jndi)) {
                ReflectionUtils.setFieldValue(realField, testInstance, context.lookup(jndi));
                continue;
            }
            String mappedName = getMappedName(field);
            if (StringUtils.isNotBlank(mappedName)) {
                ReflectionUtils.setFieldValue(realField, testInstance, context.lookup(field.getType(), mappedName));
                continue;
            }
            ReflectionUtils.setFieldValue(realField, testInstance, context.getBean(field.getType()));
        }
        this.next.evaluate();
    }

    private String getMappedName(FrameworkField field) {
        Field realField = field.getField();
        EJB ann = realField.getAnnotation(EJB.class);
        if (StringUtils.isNotBlank(ann.mappedName())) {
            return ann.mappedName();
        }
        if (StringUtils.isNotBlank(ann.beanName())) {
            return ann.beanName();
        }
        return null;
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
