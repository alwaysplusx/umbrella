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
public class EclipselinkTest {

    @Autowired
    private TeacherDao teacherDao;

    @PersistenceContext(unitName = "moon.eclipselink")
    private EntityManager em;

    @Test
    public void testSetUp() {
        assertNotNull(em);
        Object result = em.createNativeQuery("select 1 from dual").getSingleResult();
        assertEquals("1", String.valueOf(result));
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Student> query = cb.createQuery(Student.class);
        Root<Student> root = query.from(Student.class);
        Predicate p1 = cb.and();
        Predicate p2 = cb.equal(root.get("studentName"), "wuxii");
        System.out.println(p1);
        System.out.println(p2);
    }

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

    @Test
    public void testFetch() {
        Teacher teacher = teacherDao.findOne(new Specification<Teacher>() {
            @Override
            public Predicate toPredicate(Root<Teacher> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                // query.distinct(true);
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
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Teacher> query = cb.createQuery(Teacher.class);
        Root<Teacher> root = query.from(Teacher.class);
        root.fetch("students");
        // query.distinct(true);
        query.where(cb.equal(root.get("teacherId"), 1l));
        Teacher teacher = em.createQuery(query).getSingleResult();
        assertNotNull(teacher);
    }

}
