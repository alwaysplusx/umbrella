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
package com.harmony.umbrella.validator;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.Set;

import javax.validation.Configuration;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.Test;

/**
 * @author wuxii@foxmail.com
 */
public class ValidatorTest {

    public static void main(String[] args) throws Exception {
        Configuration<?> configure = Validation.byDefaultProvider()//
                .configure();
        ValidatorFactory validatorFactory = configure.buildValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        print(validator.validate(new Foo()));

    }

    @Test
    public void testAddMapping() throws Exception {

        Validator validator = Validation.byDefaultProvider().configure()//
                .addMapping(new FileInputStream(new File("src/test/resources/constraints-bar.xml")))//
                .buildValidatorFactory().getValidator();
        print(validator.validate(new Bar()));
        
    }

    private static void print(Set<?> set) {
        print(set.iterator());
    }

    private static void print(Iterator<?> it) {
        while (it.hasNext()) {
            System.out.println(it.next());
        }
    }

}
