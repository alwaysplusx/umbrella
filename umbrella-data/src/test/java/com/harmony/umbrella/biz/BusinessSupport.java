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
import java.util.List;

import com.harmony.umbrella.biz.Business;
import com.harmony.umbrella.data.Bond;
import com.harmony.umbrella.data.BondParser;
import com.harmony.umbrella.data.Dao;
import com.harmony.umbrella.data.EntityMetadata;
import com.harmony.umbrella.data.QBond;
import com.harmony.umbrella.data.domain.Model;
import com.harmony.umbrella.data.query.DefaultEntityMetadata;
import com.harmony.umbrella.data.query.SpecificationTransform;

/**
 * @author wuxii@foxmail.com
 */
public abstract class BusinessSupport<T extends Model<ID>, ID extends Serializable> implements Business<T, ID> {

    protected final EntityMetadata<T, ID> entityMeta;

    protected BondParser parser = SpecificationTransform.getInstance();

    public BusinessSupport(Class<T> entityClass) {
        this.entityMeta = new DefaultEntityMetadata<T, ID>(entityClass);
    }

    protected abstract Dao getDao();

    @Override
    public T save(T entity) {
        return getDao().save(entity);
    }

    @Override
    public T update(T entity) {
        return getDao().update(entity);
    }

    @Override
    public void delete(T entity) {
        getDao().delete(entity);
    }

    @Override
    public void delete(Iterable<T> entities) {
        getDao().delete(entities);
    }

    @Override
    public void deleteById(ID id) {
        getDao().delete(entityMeta.getJavaType(), id);
    }

    @Override
    public void deleteByIds(Iterable<ID> ids) {
        getDao().delete(entityMeta.getJavaType(), ids);
    }

    @Override
    public int delete(Bond bond) {
        QBond qBond = toQBond(bond);
        return getDao().executeUpdate(qBond.getDeleteQuery(), qBond.getParams());
    }

    @Override
    public T findOne(ID id) {
        return getDao().findOne(entityMeta.getJavaType(), id);
    }

    @Override
    public T findOne(Bond bond) {
        QBond qBond = toQBond(bond);
        return getDao().findOne(qBond.getQuery(), qBond.getParams());
    }

    @Override
    public List<T> findAll(Bond bond) {
        QBond qBond = toQBond(bond);
        return getDao().findAll(qBond.getQuery(), qBond.getParams());
    }

    @Override
    public List<T> findAll() {
        return getDao().findAll(entityMeta.getJavaType());
    }

    @Override
    public long count(Bond bond) {
        QBond qBond = toQBond(bond);
        return getDao().count(qBond.getCountQuery(), qBond.getParams());
    }

    @Override
    public long countAll() {
        return getDao().countAll(entityMeta.getJavaType());
    }

    @Override
    public boolean isNew(T entity) {
        return getDao().isNew(entity);
    }

    protected QBond toQBond(Bond bond) {
        return parser.toQBond(entityMeta.getEntityName(), bond);
    }

}
