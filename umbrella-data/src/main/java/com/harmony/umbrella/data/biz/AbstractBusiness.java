package com.harmony.umbrella.data.biz;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.harmony.umbrella.data.Bond;
import com.harmony.umbrella.data.BondParser;
import com.harmony.umbrella.data.Business;
import com.harmony.umbrella.data.Dao;
import com.harmony.umbrella.data.EntityMetadata;
import com.harmony.umbrella.data.QBond;
import com.harmony.umbrella.data.domain.Model;
import com.harmony.umbrella.data.domain.Page;
import com.harmony.umbrella.data.domain.PageRequest;
import com.harmony.umbrella.util.GenericUtils;

/**
 * @author wuxii@foxmail.com
 */
public abstract class AbstractBusiness<T extends Model<ID>, ID extends Serializable> implements Business<T, ID> {

    private Class<T> entityClass;

    protected abstract BondParser getBondParser();

    protected abstract Dao getDao();

    @SuppressWarnings("unchecked")
    protected Class<T> getEntityClass() {
        if (entityClass == null) {
            entityClass = (Class<T>) GenericUtils.getTargetGeneric(getClass(), AbstractBusiness.class, 0);
        }
        return entityClass;
    }

    @SuppressWarnings("unchecked")
    protected EntityMetadata<T, ID> getEntityMetadata() {
        return (EntityMetadata<T, ID>) getDao().getEntityMetadata(getEntityClass());
    }

    protected String getEntityName() {
        return getEntityMetadata().getEntityName();
    }

    protected String getTableName() {
        return getEntityMetadata().getTableName();
    }

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
    public T deleteById(ID id) {
        return getDao().delete(getEntityClass(), id);
    }

    @Override
    public void deleteByIds(Iterable<ID> ids) {
        getDao().delete(getEntityClass(), ids);
    }

    @Override
    public int delete(Bond bond) {
        QBond qBond = getBondParser().toQBond(getEntityName(), bond);
        return getDao().executeUpdate(qBond.getDeleteQuery(), qBond.getParams());
    }

    @Override
    public T findOne(ID id) {
        return getDao().findOne(getEntityClass(), id);
    }

    @Override
    public T findOne(Bond bond) {
        QBond qBond = getBondParser().toQBond(getEntityName(), bond);
        return getDao().findOne(qBond.getQuery(), qBond.getParams());
    }

    @Override
    public List<T> findAll() {
        return getDao().findAll(getEntityClass());
    }

    @Override
    public List<T> findAll(Bond bond) {
        QBond qBond = getBondParser().toQBond(getEntityName(), bond);
        return getDao().findAll(qBond.getQuery(), qBond.getParams());
    }

    @Override
    public List<T> findAll(Iterable<ID> ids) {
        List<T> result = new ArrayList<T>();
        for (ID id : ids) {
            result.add(findOne(id));
        }
        return result;
    }

    @Override
    public long count(Bond bond) {
        QBond qBond = getBondParser().toQBond(getEntityName(), bond);
        return getDao().count(qBond.getCountQuery(), qBond.getParams());
    }

    @Override
    public long countAll() {
        return getDao().countAll(getEntityClass());
    }

    @Override
    public boolean exists(ID id) {
        return findOne(id) != null;
    }

    @Override
    public boolean exists(Bond bond) {
        return !findAll(bond).isEmpty();
    }

    @Override
    public boolean isNew(T entity) {
        return getDao().isNew(entity);
    }

    @Override
    public Page<T> page(Bond bond, PageRequest pageRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Page<T> page(PageRequest pageRequest) {
        throw new UnsupportedOperationException();
    }

}
