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

import java.util.Calendar;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.harmony.umbrella.data.bond.Bond.Link;
import com.harmony.umbrella.data.query.SpecificationTransform;
import com.harmony.umbrella.examples.data.dao.StudentDao;
import com.harmony.umbrella.examples.data.persistence.Student;

/**
 * @author wuxii@foxmail.com
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/applicationContext.xml")
public class BondBuilderTest {

    private static final BondBuilder builder = new BondBuilder();
    private static final SpecificationTransform st = new SpecificationTransform();

    @Autowired
    private StudentDao stuDao;

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

    @Test
    public void testIn() {
        Bond bond = builder.in("studentId", 1l, 2l);
        assertNotNull(stuDao.findAllBySQL(st.toSQL(Student.class, bond)));
        QBond qBond = st.toXQL(Student.class, bond);
        assertNotNull(stuDao.findAll(qBond.getXQL(), qBond.getParams()));
        assertNotNull(stuDao.findAll(toSpecification(Student.class, bond)));
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
    @Ignore
    public void testAsc() {
        fail("Not yet implemented");
    }

    @Test
    @Ignore
    public void testDesc() {
        fail("Not yet implemented");
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

}
