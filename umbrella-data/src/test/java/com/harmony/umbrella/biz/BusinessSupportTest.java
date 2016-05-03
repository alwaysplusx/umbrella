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
public abstract class BusinessSupportTest<T extends Model<ID>, ID extends Serializable> implements Business<T, ID> {

    protected final EntityMetadata<T, ID> entityMeta;

    protected BondParser parser = SpecificationTransform.getInstance();

    public BusinessSupportTest(Class<T> entityClass) {
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
    public T deleteById(ID id) {
        return getDao().delete(entityMeta.getJavaType(), id);
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
