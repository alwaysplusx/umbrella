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
package com.harmony.umbrella.asm;

import java.io.IOException;

import com.harmony.umbrella.io.Resource;
import com.harmony.umbrella.io.support.PathMatchingResourcePatternResolver;
import com.harmony.umbrella.io.support.ResourcePatternResolver;
import com.harmony.umbrella.util.ResourceScaner;

/**
 * @author wuxii@foxmail.com
 */
public class ClassReadTest {

    static ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    public static void main(String[] args) throws IOException {
        Resource[] path = ResourceScaner.scanPath("com/harmony/**/ClassUtils.class");
        for (Resource resource : path) {
            ClassReader reader = new ClassReader(resource.getInputStream());
            String className = reader.getClassName();
            System.out.println(className);
        }
        /*Resource[] resources = resourcePatternResolver.getResources("");
        for (Resource resource : resources) {
            System.out.println(resource);
        }*/

    }
}
