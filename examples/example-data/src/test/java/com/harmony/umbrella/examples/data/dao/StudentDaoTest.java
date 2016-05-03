package com.harmony.umbrella.examples.data.dao;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.harmony.umbrella.examples.data.persistence.Student;

/**
 * @author wuxii@foxmail.com
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/applicationContext.xml")
public class StudentDaoTest {

    @Autowired
    private StudentDao stuDao;

    @Test
    public void testFindOne() {
        Student stu = stuDao.findOne(1l);
        assertNotNull(stu);
    }

}
