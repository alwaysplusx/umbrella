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
package com.harmony.umbrella.data.bond;

import static com.harmony.umbrella.data.query.SpecificationTransform.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.harmony.umbrella.data.bond.Bond.Link;
import com.harmony.umbrella.data.domain.Sort;
import com.harmony.umbrella.data.query.SpecificationTransform;
import com.harmony.umbrella.examples.data.dao.StudentDao;
import com.harmony.umbrella.examples.data.persistence.Student;

/**
 * @author wuxii@foxmail.com
 */
@RunWith(SpringJUnit4ClassRunner.class)
// @ContextConfiguration(locations = "classpath:/applicationContext.xml")
// @ContextConfiguration(locations =
// "classpath:/com/harmony/umbrella/examples/eclipselink/applicationContext.xml")
@ContextConfiguration(locations = "classpath:/com/harmony/umbrella/examples/hibernate/applicationContext.xml")
public class BondBuilderTest {

    private static final BondBuilder builder = new BondBuilder();
    private static final SpecificationTransform st = SpecificationTransform.getInstance();

    @Autowired
    private StudentDao stuDao;

    @PersistenceContext(unitName = "moon.hibernate")
    private EntityManager em;

    @Test
    public void testEqual() {
        Bond bond = builder.equal("studentName", "stu1");

        String sql = st.toSQL(Student.class, bond);
        assertNotNull(stuDao.findAllBySQL(sql, Student.class));

        QBond qBond = st.toXQL(Student.class, bond);
        assertNotNull(stuDao.findAll(qBond.getXQL(), qBond.getParams()));

        assertNotNull(stuDao.findAll(toSpecification(Student.class, bond)));
    }

    @Test
    @Ignore
    public void testNotEqual() {
        Bond bond = builder.notEqual("studentName", "stu1");

        assertNotNull(stuDao.findAllBySQL(st.toSQL(Student.class, bond)));

        QBond qBond = st.toXQL(Student.class, bond);
        assertNotNull(stuDao.findAll(qBond.getXQL(), qBond.getParams()));

        assertNotNull(stuDao.findAll(toSpecification(Student.class, bond)));
    }

    @Test(expected = IllegalArgumentException.class)
    @Ignore
    public void testHibernateIn() {
        Query query = em.createQuery("select o from Student o where (o.studentId in :studentId)");
        query.setParameter("studentId", Arrays.asList(1l, 2l));
        @SuppressWarnings("unchecked")
        List<Student> result = query.getResultList();
        System.out.println(result);
    }

    @Test
    public void testIn() {
        Bond bond = builder.in("studentId", 1l, 2l);
        // assertNotNull(stuDao.findAllBySQL(st.toSQL(Student.class, bond)));
        QBond qBond = st.toXQL(Student.class, bond);
        assertNotNull(stuDao.findAll(qBond.getXQL(), qBond.getParams()));
    }

    @Test
    @Ignore
    public void testNotIn() {
    }

    @Test
    public void testIsNull() {
        Bond bond = builder.isNull("studentName");
        assertNotNull(stuDao.findAllBySQL(st.toSQL(Student.class, bond)));
        QBond qBond = st.toXQL(Student.class, bond);
        assertNotNull(stuDao.findAll(qBond.getXQL(), qBond.getParams()));
        assertNotNull(stuDao.findAll(toSpecification(Student.class, bond)));
    }

    @Test
    @Ignore
    public void testIsNotNull() {
    }

    @Test
    public void testLike() {
        Bond bond = builder.like("studentName", "%stu%");
        assertNotNull(stuDao.findAllBySQL(st.toSQL(Student.class, bond)));
        QBond qBond = st.toXQL(Student.class, bond);
        assertNotNull(stuDao.findAll(qBond.getXQL(), qBond.getParams()));
        assertNotNull(stuDao.findAll(toSpecification(Student.class, bond)));
    }

    @Test
    @Ignore
    public void testNotLike() {
    }

    @Test
    public void testGe() {
        Bond bond = builder.ge("studentId", 2l);
        assertNotNull(stuDao.findAllBySQL(st.toSQL(Student.class, bond)));
        QBond qBond = st.toXQL(Student.class, bond);
        assertNotNull(stuDao.findAll(qBond.getXQL(), qBond.getParams()));
        assertNotNull(stuDao.findAll(toSpecification(Student.class, bond)));

        bond = builder.ge("birthday", Calendar.getInstance());
        // assertNotNull(stuDao.findAllBySQL(st.toSQL(Student.class, bond)));
        qBond = st.toXQL(Student.class, bond);
        assertNotNull(stuDao.findAll(qBond.getXQL(), qBond.getParams()));
        assertNotNull(stuDao.findAll(toSpecification(Student.class, bond)));
    }

    @Test
    @Ignore
    public void testGt() {
    }

    @Test
    @Ignore
    public void testLe() {
    }

    @Test
    @Ignore
    public void testLt() {
    }

    @Test
    public void testInline() {
        Bond bond = builder.inline("studentId", "teacherId", Link.EQUAL);
        assertNotNull(stuDao.findAllBySQL(st.toSQL(Student.class, bond)));

        bond = builder.inline("studentId", "teacher.teacherId", Link.EQUAL);
        QBond qBond = st.toXQL(Student.class, bond);
        assertNotNull(stuDao.findAll(qBond.getXQL(), qBond.getParams()));

        assertNotNull(stuDao.findAll(toSpecification(Student.class, bond)));
    }

    @Test
    public void testAsc() {
        Sort sort = builder.asc("studentName");
        Bond bond = builder.equal("studentName", "stu1");
        String sql = st.toSQL(Student.class, sort, bond);
        System.err.println(sql);
        assertNotNull(stuDao.findAllBySQL(sql));
    }

    @Test
    @Ignore
    public void testDesc() {
    }

    @Test
    public void testAnd() {
        Bond bond = builder.and(builder.equal("studentName", "stu1"), builder.ge("studentId", 2l));
        assertNotNull(stuDao.findAllBySQL(st.toSQL(Student.class, bond)));
        QBond qBond = st.toXQL(Student.class, bond);
        assertNotNull(stuDao.findAll(qBond.getXQL(), qBond.getParams()));
        assertNotNull(stuDao.findAll(toSpecification(Student.class, bond)));
    }

    @Test
    public void testOr() {
        Bond bond = builder.or(builder.equal("studentName", "stu1"), builder.ge("studentId", 2l));
        assertNotNull(stuDao.findAllBySQL(st.toSQL(Student.class, bond)));
        QBond qBond = st.toXQL(Student.class, bond);
        assertNotNull(stuDao.findAll(qBond.getXQL(), qBond.getParams()));
        assertNotNull(stuDao.findAll(toSpecification(Student.class, bond)));
    }

    @Test
    public void testSpec() {
        Bond bond = builder.and(builder.equal("studentName", "stu1"), builder.in("studentId", 1l, 2l));
        QBond qBond = st.toXQL(Student.class, bond);
        System.out.println(qBond);
        assertNotNull(stuDao.findAll(qBond.getXQL(), qBond.getParams()));
    }

}
