/*
 * Copyright 2002-2015 the original author or authors.
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
package com.harmony.umbrella.biz;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import com.harmony.umbrella.data.Bond;
import com.harmony.umbrella.data.BondParser;
import com.harmony.umbrella.data.JpaDao;
import com.harmony.umbrella.data.Specification;
import com.harmony.umbrella.data.domain.Model;
import com.harmony.umbrella.data.domain.Page;
import com.harmony.umbrella.data.domain.PageRequest;
import com.harmony.umbrella.data.query.SpecificationTransform;

/**
 * @author wuxii@foxmail.com
 */
public abstract class AbstractBusiness<T extends Model<ID>, ID extends Serializable> implements Business<T, ID> {

    private Class<T> entityClass;

    protected BondParser parser = SpecificationTransform.getInstance();

    public AbstractBusiness() {
    }

    public AbstractBusiness(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected abstract JpaDao<T, ID> getJpaDao();

    @SuppressWarnings("unchecked")
    private Class<T> getEntityClass() {
        if (entityClass == null) {
            ParameterizedType pt = getParameterizedType(getClass());
            if (pt != null) {
                entityClass = (Class<T>) pt.getActualTypeArguments()[0];
            }
        }
        return entityClass;
    }

    protected ParameterizedType getParameterizedType(Class<?> subClass) {
        return findParameterizedType(getClass(), AbstractBusiness.class);
    }

    protected final ParameterizedType findParameterizedType(Class<?> clazz, Class<?> targetSuperClass) {
        Class<?> superclass = clazz.getSuperclass();
        if (superclass == Object.class) {
            return null;
        } else if (targetSuperClass.equals(superclass)) {
            return (ParameterizedType) clazz.getGenericSuperclass();
        }
        return findParameterizedType(superclass, targetSuperClass);
    }

    @Override
    public T save(T entity) {
        return getJpaDao().save(entity);
    }

    @Override
    public T update(T entity) {
        return getJpaDao().update(entity);
    }

    @Override
    public void delete(T entity) {
        getJpaDao().delete(entity);
    }

    @Override
    public void delete(Iterable<T> entities) {
        getJpaDao().delete(entities);
    }

    @Override
    public int delete(Bond bond) {
        Specification<T> spec = toSpec(bond);
        return getJpaDao().delete(spec);
    }

    @Override
    public void deleteById(ID id) {
        getJpaDao().delete(id);
    }

    @Override
    public void deleteByIds(Iterable<ID> ids) {
        getJpaDao().delete(ids);
    }

    @Override
    public T findOne(ID id) {
        return getJpaDao().findById(id);
    }

    @Override
    public T findOne(Bond bond) {
        return getJpaDao().findOne(toSpec(bond));
    }

    @Override
    public List<T> findAll() {
        return getJpaDao().findAll();
    }

    @Override
    public List<T> findAll(Iterable<ID> ids) {
        return getJpaDao().findAll(ids);
    }

    @Override
    public List<T> findAll(Bond bond) {
        return getJpaDao().findAll(toSpec(bond));
    }

    @Override
    public long countAll() {
        return getJpaDao().count();
    }

    @Override
    public long count(Bond bond) {
        return getJpaDao().count(toSpec(bond));
    }

    @Override
    public boolean isNew(T entity) {
        return getJpaDao().isNew(entity);
    }

    @Override
    public Page<T> page(PageRequest pageRequest) {
        return getJpaDao().findAll(pageRequest);
    }

    @Override
    public Page<T> page(Bond bond, PageRequest pageRequest) {
        return getJpaDao().findAll(toSpec(bond), pageRequest);
    }

    @Override
    public boolean exists(ID id) {
        return getJpaDao().exists(id);
    }

    @Override
    public boolean exists(Bond bond) {
        return getJpaDao().exists(toSpec(bond));
    }

    protected Specification<T> toSpec(Bond... bond) {
        return parser.toSpecification(getEntityClass(), bond);
    }

    public void setBondParser(BondParser parser) {
        this.parser = parser;
    }

    public BondParser getBondParser() {
        return parser;
    }
}
