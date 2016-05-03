package com.harmony.umbrella.examples.data.dao;

import javax.persistence.EntityManager;

import com.harmony.umbrella.data.dao.support.JpaDaoSupport;
import com.harmony.umbrella.data.query.EntityInformation;
import com.harmony.umbrella.examples.data.persistence.Student;

/**
 * @author wuxii@foxmail.com
 */
public class StudentDaoImpl extends JpaDaoSupport<Student, Long> implements StudentDao {

    private EntityManager entityManager;

    @Override
    protected EntityInformation<Student, Long> getEntityInformation() {
        return getEntityInformation(Student.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

}
