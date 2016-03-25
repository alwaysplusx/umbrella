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

import org.junit.Test;

import com.harmony.umbrella.io.Resource;
import com.harmony.umbrella.io.ResourceManager;

/**
 * @author wuxii@foxmail.com
 */
public class ResourceManagerTest {

    public static void main(String[] args) {

        Resource[] resources = ResourceManager.getInstance().getResources("com.harmony");
        for (Resource resource : resources) {
            System.out.println(resource.toString());
        }

        Class<?>[] classes = ResourceManager.getInstance().getClasses("com.harmony");
        for (Class<?> clazz : classes) {
            System.out.println(clazz.getName());
        }

    }

    @Test
    public void testGetResources() {
        Resource[] resources = ResourceManager.getInstance().getResources("com.harmony");
        for (Resource resource : resources) {
            System.out.println(resource.toString());
        }
    }

}
