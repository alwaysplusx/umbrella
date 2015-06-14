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
package com.harmony.umbrella.examples.data;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.junit.BeforeClass;
import org.junit.Test;

import com.harmony.umbrella.data.dao.Dao;
import com.harmony.umbrella.data.dao.support.SimpleDao;
import com.harmony.umbrella.examples.data.persistence.Student;
import com.harmony.umbrella.examples.data.persistence.Teacher;

/**
 * @author wuxii@foxmail.com
 */
public class TeacherTest {

    private static EntityManager em;
    private static Dao dao;

    @BeforeClass
    public static void setUp() {
        em = Persistence.createEntityManagerFactory("umbrella").createEntityManager();
        dao = new SimpleDao(em);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetTeacher() {
        Query query = em.createQuery("select o from Teacher o where o.teacherId=1");
        List<Teacher> results = query.getResultList();
        for (Teacher teacher : results) {
            System.out.println(teacher);
            for (Student stu : teacher.getStudents()) {
                System.out.println(stu);
            }
        }
    }

    @Test
    public void testGetTeacherByDao() {
        List<Teacher> results = dao.findAll("select o from Teacher o where o.teacherId=1");
        for (Teacher teacher : results) {
            System.out.println(teacher);
            for (Student stu : teacher.getStudents()) {
                System.out.println(stu);
            }
        }
    }

}
