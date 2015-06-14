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

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.LazyInitializationException;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.harmony.umbrella.data.dao.Dao;
import com.harmony.umbrella.data.domain.Specification;
import com.harmony.umbrella.examples.data.dao.TeacherDao;
import com.harmony.umbrella.examples.data.persistence.Student;
import com.harmony.umbrella.examples.data.persistence.Teacher;

/**
 * @author wuxii@foxmail.com
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/applicationContext.xml" })
public class TeacherTest2 {

    @Autowired
    private Dao dao;

    @Autowired
    private TeacherDao teacherDao;

    @Test
    public void testInster() {
        Teacher teacher = new Teacher();
        teacher.setTeacherName("tn");
        dao.save(teacher);
    }

    @Test(expected = LazyInitializationException.class)
    public void testGetTeacher() {
        List<Teacher> results = dao.findAll("select o from Teacher o where o.teacherId=1");
        for (Teacher teacher : results) {
            System.out.println(teacher);
            for (Student stu : teacher.getStudents()) {
                System.out.println(stu);
            }
        }
    }

    @Test
    @Ignore
    public void testGetTeacherByTeacherDao() {
        List<Teacher> teachers = teacherDao.findAll(new Specification<Teacher>() {
            @Override
            public Predicate toPredicate(Root<Teacher> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return null;
            }
        });
        for (Teacher teacher : teachers) {
            System.out.println(teacher);
        }
    }

}
