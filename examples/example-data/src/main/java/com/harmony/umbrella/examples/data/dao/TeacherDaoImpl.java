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
