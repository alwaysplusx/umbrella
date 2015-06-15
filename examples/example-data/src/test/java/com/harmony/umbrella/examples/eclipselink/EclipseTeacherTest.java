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
package com.harmony.umbrella.examples.eclipselink;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.harmony.umbrella.data.domain.Specification;
import com.harmony.umbrella.examples.data.dao.TeacherDao;
import com.harmony.umbrella.examples.data.persistence.Student;
import com.harmony.umbrella.examples.data.persistence.Teacher;

/**
 * @author wuxii@foxmail.com
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "applicationContext.xml")
public class EclipseTeacherTest {

    @Autowired
    private TeacherDao teacherDao;

    @PersistenceContext(unitName = "moon.eclipselink")
    private EntityManager em;

    @Test
    public void testFindAll() {
        List<Teacher> results = teacherDao.findAll("select o from Teacher o where o.teacherId=1");
        for (Teacher teacher : results) {
            System.out.println(teacher);
            for (Student stu : teacher.getStudents()) {
                System.out.println(stu);
            }
        }
    }

    @Test(expected = NonUniqueResultException.class)
    public void testFetch() {
        Teacher teacher = teacherDao.findOne(new Specification<Teacher>() {
            @Override
            public Predicate toPredicate(Root<Teacher> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                root.fetch("students");
                return cb.equal(root.get("teacherId"), 1l);
            }
        });
        assertNotNull(teacher);
        for (Student stu : teacher.getStudents()) {
            System.out.println(stu);
        }
    }

    @Test(expected = NonUniqueResultException.class)
    public void testFetchStu() {
        // FIXME eclipselink bug?
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Teacher> query = cb.createQuery(Teacher.class);
        Root<Teacher> root = query.from(Teacher.class);
        root.fetch("students");
        query.where(cb.equal(root.get("teacherId"), 1l));
        Teacher teacher = em.createQuery(query).getSingleResult();
        assertNotNull(teacher);
    }
}
