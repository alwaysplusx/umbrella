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

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import org.junit.Test;

import com.harmony.umbrella.access.AccessChain;

/**
 * @author wuxii@foxmail.com
 */
public class AccessorTest {

    private static Person person;

    //   A-B
    //    |
    //|--------|----------|       
    //C√       D---E      F
    //           |
    //      |---------|
    //      G         H

    static {
        // 以C为起点
        person = new Person("C");
        person.father = new Person("A");
        person.mother = new Person("B");

        Person brotherD = new Person("D");
        brotherD.father = person.father;
        brotherD.mother = person.mother;
        brotherD.spouse = new Person("E");

        Person g = new Person("G");
        Person h = new Person("H");

        g.brother = Arrays.asList(h);
        h.brother = Arrays.asList(g);

        brotherD.children = Arrays.asList(g, h);
        brotherD.spouse.children = brotherD.children;

        Person brotherF = new Person("F");
        brotherF.father = person.father;
        brotherF.mother = person.mother;

        person.brother = Arrays.asList(brotherD, brotherF);
    }

    @Test
    public void testAccessor() {
        AccessChain chain = AccessChain.createDefaultChain();
        assertEquals("C", chain.get("name", person));
        assertEquals("D", chain.get("brother[0].name", person));
        assertEquals("E", chain.get("brother[0].spouse.name", person));
        assertEquals("G", chain.get("brother[0].spouse.children[0].name", person));
    }

    @Test
    public void testAccessMember() {
        StringTokenizer st = new StringTokenizer("brother[0].spouse.name", ".[]");
        while (st.hasMoreTokens()) {
            System.out.println(st.nextToken(".[]"));
        }
        // CopyOfAccessMember member = new CopyOfAccessMember(Person.class, "brother[0].spouse.name");
        // member.getField();
    }

    static class Person {

        public String name;
        public Person spouse;
        public Person father;
        public Person mother;
        public List<Person> brother;
        public List<Person> children;

        public Person(String name) {
            this.name = name;
        }
    }

}
