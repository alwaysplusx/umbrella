package com.harmony.umbrella.data.result;

import com.harmony.umbrella.data.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.repository.support.PageableExecutionUtils;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.persistence.criteria.Selection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 查询结果
 *
 * @author wuxii@foxmail.com
 */
public class QueryResultImpl<T> extends AbstractQueryResult<T> {

    private final EntityManager entityManager;
    private final CriteriaBuilder criteriaBuilder;

    public QueryResultImpl(EntityManager entityManager, QueryBundle<T> bundle) {
        super(bundle);
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    @Override
    public RowResult getSingleResult(Selections selections) {
        try {
            TypedQueryBuilder<Object> builder = newBuilder(selections, Object.class);
            Object value = builder
                    .applyForSingle()
                    .build()
                    .getSingleResult();
            return new RowResult(builder.columns, value);
        } catch (NoResultException e) {
            return RowResult.empty();
        }
    }

    @Override
    public RowResult getFirstResult(Selections selections) {
        try {
            TypedQueryBuilder<Object> builder = newBuilder(selections, Object.class);
            Object value = builder
                    .applyForFirst()
                    .build(PageRequest.of(0, 1))
                    .getSingleResult();
            return new RowResult(builder.columns, value);
        } catch (NoResultException e) {
            return RowResult.empty();
        }
    }

    @Override
    public ListResult getListResult(Selections selections) {
        List<RowResult> rowList = getRowList(selections, newPageable());
        return new ListResult(rowList);
    }

    @Override
    public ListResult getAllResult(Selections selections) {
        TypedQueryBuilder<Object> builder = newBuilder(selections, Object.class);
        List<Object> values = builder
                .applyForList()
                .build()
                .getResultList();
        return new ListResult(builder.columns, values);
    }

    @Override
    public PageResult getPageResult(Selections selections, Pageable pageable) {
        List<RowResult> rowList = getRowList(selections, pageable);
        return new PageResult(PageableExecutionUtils.getPage(rowList, pageable, this::count));
    }

    @Override
    public long count(Selections selections) {
        return newBuilder(selections, Long.class)
                .applyForCount()
                .build()
                .getSingleResult();
    }

    protected List<RowResult> getRowList(Selections selections, Pageable pageable) {
        TypedQueryBuilder<Object> builder = newBuilder(selections, Object.class);
        List<Column> columns = builder.columns;
        List<Object> values = builder
                .applyForList()
                .build(pageable)
                .getResultList();
        return values.stream().map(v -> new RowResult(columns, v)).collect(Collectors.toList());
    }

    protected <R> TypedQueryBuilder<R> newBuilder(Selections selections, Class<R> resultClass) {
        return new TypedQueryBuilder<>(selections, resultClass);
    }

    /**
     * 查询构建器, 同时也是criteriaQuery的Holder
     *
     * @param <M>
     */
    class TypedQueryBuilder<M> {

        private final Root<T> root;
        private final CriteriaQuery<M> query;
        private final List<Column> columns;
        private final List<Selection<?>> expressions;

        TypedQueryBuilder(Selections selections, Class<M> resultClass) {
            this.query = criteriaBuilder.createQuery(resultClass);
            this.root = query.from(domainClass);
            this.columns = selections.generate(root, query, criteriaBuilder);
            this.expressions = columns.stream().map(Column::getExpression).collect(Collectors.toList());
        }

        // build methods

        public TypedQuery<M> build() {
            return build(null);
        }

        public TypedQuery<M> build(Pageable pageable) {
            if (expressions.isEmpty()) {
                throw new QueryException("select fields not found.");
            }
            if (expressions.size() == 1) {
                query.select((javax.persistence.criteria.Selection) expressions.get(0));
            } else {
                query.multiselect(expressions);
            }
            if (pageable != null) {
                query.orderBy(QueryUtils.toOrders(pageable.getSort(), root, criteriaBuilder));
            }
            TypedQuery typedQuery = entityManager.createQuery(query);
            if (pageable != null) {
                typedQuery.setFirstResult((int) pageable.getOffset())
                        .setMaxResults(pageable.getPageSize());
            }
            return typedQuery;
        }

        // quick methods

        /**
         * 构建统计查询
         */
        public TypedQueryBuilder<M> applyForCount() {
            return applySpecification()
                    .applyJoin()
                    .applyGrouping()
                    .applyFetch();
        }

        /**
         * 构建单结果查询
         */
        public TypedQueryBuilder<M> applyForSingle() {
            return applySpecification()
                    .applyFetch()
                    .applyGrouping()
                    .applyJoin();
        }

        /**
         * 构建首个结果查询
         */
        public TypedQueryBuilder<M> applyForFirst() {
            return applyForSingle().applySort();
        }

        /**
         * 构建列表结果集查询
         */
        public TypedQueryBuilder<M> applyForList() {
            return applySpecification()
                    .applyFetch()
                    .applyGrouping()
                    .applyJoin()
                    .applySort();
        }

        // apply methods

        public TypedQueryBuilder<M> applySpecification() {
            Specification<T> specification = bundle.getSpecification();
            if (specification != null) {
                Predicate predicate = specification.toPredicate(root, query, criteriaBuilder);
                if (predicate != null) {
                    query.where(predicate);
                }
            }
            return this;
        }

        public TypedQueryBuilder<M> applyJoin() {
            QueryBuilder.JoinAttributes joinAttr = bundle.getJoinAttributes();
            if (joinAttr != null && !joinAttr.getAttributes().isEmpty()) {
                for (QueryBuilder.Attribute attr : joinAttr) {
                    String name = attr.getName();
                    JoinType joniType = attr.getJoniType();
                    if (!isAlreadyJoin(name, joniType)) {
                        root.join(name, joniType);
                    }
                }
            }
            return this;
        }

        public TypedQueryBuilder<M> applyFetch() {
            QueryBuilder.FetchAttributes fetchAttr = bundle.getFetchAttributes();
            if (fetchAttr != null && !fetchAttr.getAttributes().isEmpty()) {
                for (QueryBuilder.Attribute attr : fetchAttr) {
                    String name = attr.getName();
                    JoinType joniType = attr.getJoniType();
                    if (!isAlreadyFetch(name, joniType)) {
                        root.fetch(name, joniType);
                    }
                }
            }
            return this;
        }

        public TypedQueryBuilder<M> applyGrouping() {
            Selections grouping = bundle.getGrouping();
            if (grouping != null) {
                query.groupBy(grouping.generateExpressions(root, query, criteriaBuilder));
            }
            return this;
        }

        public TypedQueryBuilder<M> applySort() {
            Sort sort = bundle.getSort();
            if (sort != null) {
                query.orderBy(QueryUtils.toOrders(sort, root, criteriaBuilder));
            }
            return this;
        }

        private boolean isAlreadyFetch(String name, JoinType joinType) {
            return root
                    .getFetches()
                    .stream()
                    .filter(e -> e.getAttribute().getName().equals(name))
                    .anyMatch(e -> e.getJoinType().equals(joinType));
        }

        private boolean isAlreadyJoin(String name, JoinType joinType) {
            return root
                    .getJoins()
                    .stream()
                    .filter(e -> e.getAttribute().getName().equals(name))
                    .anyMatch(e -> e.getJoinType().equals(joinType));
        }

    }

}
