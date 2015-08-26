/*
 * Copyright 2013-2015 wuxii@foxmail.com.
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
package com.harmony.umbrella.data.dao;

import java.io.Serializable;

import javax.persistence.EntityManager;

/**
 * @author wuxii@foxmail.com
 */
public class SimpleJpaDao<E, ID extends Serializable> extends JpaDaoSupport<E, ID> {

    protected final Class<E> entityClass;

    protected final EntityManager em;

    public SimpleJpaDao(Class<E> entityClass, EntityManager entityManager) {
        this.em = entityManager;
        this.entityClass = entityClass;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    protected Class<E> getEntityClass() {
        return entityClass;
    }

}
