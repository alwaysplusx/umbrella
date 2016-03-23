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

import java.io.IOException;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import com.harmony.umbrella.context.ee.EJBApplicationContext;
import com.harmony.umbrella.io.Resource;
import com.harmony.umbrella.io.resource.DefaultResourceLoader;

/**
 * @author wuxii@foxmail.com
 */
public class EJBJUnit4ClassRunner extends BlockJUnit4ClassRunner {

    private DefaultResourceLoader resourceLoader = new DefaultResourceLoader();
    private EJBApplicationContext applicationContext;

    public EJBJUnit4ClassRunner(Class<?> klass) throws InitializationError {
        super(klass);
        try {
            this.applicationContext = createApplicationContext(klass);
        } catch (IOException e) {
            throw new InitializationError(e);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected Statement withBefores(FrameworkMethod method, Object target, Statement statement) {
        Statement junitBefores = super.withBefores(method, target, statement);
        return new EJBRunBefores(junitBefores, target, getTestClass(), applicationContext);
    }

    private EJBApplicationContext createApplicationContext(Class<?> klass) throws IOException {
        ContainerConfiguration ann = klass.getAnnotation(ContainerConfiguration.class);
        if (ann == null) {
            return EJBApplicationContext.getInstance();
        }
        Resource resource = resourceLoader.getResource(ann.location());
        return EJBApplicationContext.getInstance(resource.getURL());
    }
    
}
