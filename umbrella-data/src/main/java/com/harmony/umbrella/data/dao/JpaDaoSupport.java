package com.harmony.umbrella.data.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.harmony.umbrella.data.EntityMetadata;
import com.harmony.umbrella.data.JpaDao;
import com.harmony.umbrella.data.Specification;
import com.harmony.umbrella.data.domain.Page;
import com.harmony.umbrella.data.domain.PageImpl;
import com.harmony.umbrella.data.domain.Pageable;
import com.harmony.umbrella.data.domain.Sort;
import com.harmony.umbrella.data.util.QueryUtils;
import com.harmony.umbrella.util.Assert;
import com.harmony.umbrella.util.GenericUtils;

/**
 * @author wuxii@foxmail.com
 */
public abstract class JpaDaoSupport<E, ID extends Serializable> extends DaoSupport implements JpaDao<E, ID> {

    public static final String DELETE_ALL_QUERY_STRING = "delete from %s x";

    private EntityMetadata<E, ID> entityMetadata;

    private Class<E> entityClass;

    public JpaDaoSupport() {
    }

    public JpaDaoSupport(Class<E> entityClass) {
        this.entityClass = entityClass;
    }

    @SuppressWarnings("unchecked")
    protected Class<E> getEntityClass() {
        if (entityClass == null) {
            entityClass = (Class<E>) GenericUtils.getSuperGeneric(getClass(), 0);
        }
        return entityClass;
    }

    @SuppressWarnings("unchecked")
    private EntityMetadata<E, ID> getEntityMetadata() {
        if (entityMetadata == null) {
            entityMetadata = (EntityMetadata<E, ID>) getEntityMetadata(getEntityClass());
        }
        return entityMetadata;
    }

    protected String getEntityName() {
        return getEntityMetadata().getEntityName();
    }

    @Override
    public Page<E> findAll(Pageable pageable) {

        if (null == pageable) {
            return new PageImpl<E>(findAll());
        }

        return findAll(null, pageable);
    }

    @Override
    public Iterable<E> findAll(Sort sort) {
        return getQuery(null, sort).getResultList();
    }

    @Override
    public long count() {
        return countAll(getEntityClass());
    }

    @Override
    public E findById(ID id) {
        return findOne(getEntityClass(), id);
    }

    @Override
    public List<E> findAll() {
        return findAll(getEntityClass());
    }

    @Override
    public List<E> findAll(Iterable<ID> ids) {
        Assert.notNull(ids, "ids is null");
        EntityMetadata<E, ID> entityMetadata = getEntityMetadata();
        if (entityMetadata.hasCompositeId()) {
            List<E> results = new ArrayList<E>();
            for (ID id : ids) {
                results.add(findById(id));
            }
            return results;
        }

        ByIdsSpecification<E> specification = new ByIdsSpecification<E>(entityMetadata);
        TypedQuery<E> query = getQuery(specification, (Sort) null);

        return query.setParameter(specification.parameter, ids).getResultList();
    }

    @Override
    public void flush() {
        getEntityManager().flush();
    }

    @Override
    public E saveAndFlush(E entity) {
        E result = save(entity);
        flush();
        return result;
    }

    @Override
    public int delete(Specification<E> spec) {
        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaDelete<E> critDel = builder.createCriteriaDelete(getEntityClass());
        Root<E> root = critDel.from(getEntityClass());
        critDel.where(spec.toPredicate(root, null, builder));
        return getEntityManager().createQuery(critDel).executeUpdate();
    }

    @Override
    public void deleteAll() {
        deleteAll(getEntityClass());
    }

    @Override
    public void deleteInBatch(Iterable<E> entities) {
        if (entities == null) {
            return;
        }
        if (!entities.iterator().hasNext()) {
            return;
        }

        StringBuilder buffer = new StringBuilder();

        String sqlBegin = String.format(DELETE_ALL_QUERY_STRING, getEntityName());

        buffer.append(sqlBegin).append(" where");

        Iterator<E> iterator = entities.iterator();

        int i = 0;

        while (iterator.hasNext()) {

            iterator.next();

            buffer.append(String.format(" %s = ?%d", "x", ++i));

            if (iterator.hasNext()) {
                buffer.append(" or");
            }
        }

        Query query = getEntityManager().createQuery(buffer.toString());

        i = 0;
        iterator = entities.iterator();

        while (iterator.hasNext()) {
            query.setParameter(++i, iterator.next());
        }

        query.executeUpdate();

    }

    @Override
    public void deleteById(ID id) {
        delete(getEntityClass(), id);
    }

    @Override
    public void deleteByIds(Iterable<ID> ids) {
        for (ID id : ids) {
            deleteById(id);
        }
    }

    @Override
    public void deleteAllInBatch() {
        deleteAll(getEntityClass());
    }

    @Override
    public E getOne(ID id) {
        if (id == null)
            return null;
        return getEntityManager().getReference(getEntityClass(), id);
    }

    @Override
    public E findOne(Specification<E> spec) {
        try {
            return getQuery(spec, null).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<E> findAll(Specification<E> spec) {
        return getQuery(spec, null).getResultList();
    }

    @Override
    public Page<E> findAll(Specification<E> spec, Pageable pageable) {

        TypedQuery<E> query = getQuery(spec, pageable != null ? pageable.getSort() : null);

        return pageable == null ? new PageImpl<E>(query.getResultList()) : readPage(query, pageable, spec);
    }

    @Override
    public List<E> findAll(Specification<E> spec, Sort sort) {
        return getQuery(spec, sort).getResultList();
    }

    @Override
    public long count(Specification<E> spec) {
        return executeCountQuery(getCountQuery(spec));
    }

    @Override
    public boolean exists(ID id) {
        if (id == null) {
            return false;
        }

        return findById(id) != null;
    }

    @Override
    public E findOneBySQL(String sql) {
        return findOneBySQL(sql, getEntityClass());
    }

    @Override
    public E findOneBySQL(String sql, Map<String, Object> parameters) {
        return findOneBySQL(sql, getEntityClass(), parameters);
    }

    @Override
    public E findOneBySQL(String sql, Object... parameters) {
        return findOneBySQL(sql, getEntityClass(), parameters);
    }

    @Override
    public List<E> findAllBySQL(String sql) {
        return findAllBySQL(sql, getEntityClass());
    }

    @Override
    public List<E> findAllBySQL(String sql, Map<String, Object> parameters) {
        return findAllBySQL(sql, getEntityClass(), parameters);
    }

    @Override
    public List<E> findAllBySQL(String sql, Object... parameters) {
        return findAllBySQL(sql, getEntityClass(), parameters);
    }

    @Override
    public boolean exists(Specification<E> spec) {
        return executeCountQuery(getCountQuery(spec)) > 0;
    }

    protected TypedQuery<E> getQuery(Specification<E> spec, Sort sort) {

        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<E> query = builder.createQuery(getEntityClass());

        Root<E> root = applySpecificationToCriteria(spec, query);
        query.select(root);

        if (sort != null) {
            query.orderBy(QueryUtils.toJpaOrders(sort, root, builder));
        }

        return getEntityManager().createQuery(query);
    }

    protected Page<E> readPage(TypedQuery<E> query, Pageable pageable, Specification<E> spec) {
        query.setFirstResult(pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        Long total = executeCountQuery(getCountQuery(spec));
        List<E> content = total > pageable.getOffset() ? query.getResultList() : Collections.<E> emptyList();

        return new PageImpl<E>(content, pageable, total);
    }

    protected TypedQuery<Long> getCountQuery(Specification<E> spec) {

        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);

        Root<E> root = applySpecificationToCriteria(spec, query);

        if (query.isDistinct()) {
            query.select(builder.countDistinct(root));
        } else {
            query.select(builder.count(root));
        }

        return getEntityManager().createQuery(query);
    }

    private <S> Root<E> applySpecificationToCriteria(Specification<E> spec, CriteriaQuery<S> query) {

        Assert.notNull(query);
        Root<E> root = query.from(getEntityClass());

        if (spec == null) {
            return root;
        }

        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        Predicate predicate = spec.toPredicate(root, query, builder);

        if (predicate != null) {
            query.where(predicate);
        }

        if (root.getFetches().size() > 0 && !query.isDistinct()) {
            query.distinct(true);
        }

        return root;
    }

    /**
     * Executes a count query and transparently sums up all values returned.
     * 
     * @param query
     *            must not be {@literal null}.
     * @return
     */
    private static Long executeCountQuery(TypedQuery<Long> query) {

        Assert.notNull(query);

        List<Long> totals = query.getResultList();
        Long total = 0L;

        for (Long element : totals) {
            total += element == null ? 0 : element;
        }

        return total;
    }

    private static final class ByIdsSpecification<T> implements Specification<T> {

        private final EntityMetadata<T, ?> entityInformation;

        @SuppressWarnings("rawtypes")
        ParameterExpression<Iterable> parameter;

        public ByIdsSpecification(EntityMetadata<T, ?> entityInformation) {
            this.entityInformation = entityInformation;
        }

        /*
         * (non-Javadoc)
         * @see org.springframework.data.jpa.domain.Specification#toPredicate(javax.persistence.criteria.Root, javax.persistence.criteria.CriteriaQuery, javax.persistence.criteria.CriteriaBuilder)
         */
        public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

            Path<?> path = root.get(entityInformation.getIdAttribute());
            parameter = cb.parameter(Iterable.class);
            return path.in(parameter);
        }
    }

}
