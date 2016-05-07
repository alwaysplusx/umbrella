package com.harmony.umbrella.access;

import static org.junit.Assert.*;

import org.junit.Test;

import com.harmony.umbrella.core.Member;
import com.harmony.umbrella.core.MemberAccess;

/**
 * @author wuxii@foxmail.com
 */
public class MemberTest {

    private static Person self;

    static {
        // 以C为起点
        self = new Person("Me");
        self.father = new Person("A");
        self.mother = new Person("B");
        self.father.spouse = self.mother;

        self.brother = new Person("D");
        self.brother.brother = self;

    }

    static class Person {

        public String name;
        public Person spouse;
        public Person father;
        public Person mother;
        public Person brother;

        public Person(String name) {
            this.name = name;
        }

    }

    @Test
    public void testAccess() {
        Member member = MemberAccess.access(Person.class, "brother.name");
        
        assertEquals("D", member.get(self));
    }

}
