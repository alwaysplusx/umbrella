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
package com.harmony.umbrella.core;

import com.harmony.umbrella.core.accessor.AccessorChain;

/**
 * @author wuxii@foxmail.com
 */
public class AccessorTest {

    private static Person person;

    static {
        // TODO 填充数据
        person = new Person("A");
        person.family = new Family();
        person.family.me = person;
        person.family.spouse = new Person("A's spouse");
        person.family.father = new Person("A's father B");
        person.family.mother = new Person("A's father C");
        person.family.son = new Person[] { new Person("son0"), new Person("son1") };
        person.family.brother = new Person[] { new Person("brother0"), new Person("brother1") };
    }

    public static void main(String[] args) {
        AccessorChain chain = new AccessorChain();
        chain.get("family.father.brother[1].spouse.son[0]", person);
    }

    static class Person {

        public String name;
        public Family family;

        public Person(String name) {
            this.name = name;
        }
    }

    static class Family {

        public Person me;
        public Person spouse;
        public Person father;
        public Person mother;
        public Person[] son;
        public Person[] brother;

    }

}
