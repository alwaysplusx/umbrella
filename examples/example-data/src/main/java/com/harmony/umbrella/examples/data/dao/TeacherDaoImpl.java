package com.harmony.umbrella.examples.data.dao;

import javax.persistence.EntityManager;

import com.harmony.umbrella.data.dao.support.JpaDaoSupport;
import com.harmony.umbrella.data.query.EntityInformation;
import com.harmony.umbrella.examples.data.persistence.Teacher;

/**
 * @author wuxii@foxmail.com
 */
public class TeacherDaoImpl extends JpaDaoSupport<Teacher, Long> implements TeacherDao {

    private EntityManager entityManager;

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    protected EntityInformation<Teacher, Long> getEntityInformation() {
        return getEntityInformation(Teacher.class);
    }

}
