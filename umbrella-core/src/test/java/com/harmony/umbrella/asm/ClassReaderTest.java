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
package com.harmony.umbrella.asm;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import com.harmony.umbrella.io.Resource;
import com.harmony.umbrella.io.resource.FileSystemResource;

/**
 * @author wuxii@foxmail.com
 */
public class ClassReaderTest {

    @Test
    public void test() throws IOException {
        Resource resource = new FileSystemResource("target/classes/com/harmony/umbrella/core/BeanFactory.class");
        InputStream is = resource.getInputStream();

        ClassReader reader = new ClassReader(is);
        String className = reader.getClassName();

        System.out.println(className);
    }
}
