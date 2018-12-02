package com.harmony.umbrella.data.result;

import com.harmony.umbrella.data.*;
import com.harmony.umbrella.data.model.QueryModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 查询结果
 *
 * @author wuxii@foxmail.com
 */
public class QueryResultImpl<T> implements QueryResult<T> {

    private static final Logger log = LoggerFactory.getLogger(QueryResultImpl.class);

    private final QueryBundle<T> bundle;
    private final Class<T> domainClass;
    private final Specification<T> specification;

    private final EntityManager entityManager;
    private final CriteriaBuilder criteriaBuilder;

    public QueryResultImpl(EntityManager entityManager, QueryBundle<T> bundle) {
        this.bundle = bundle;
        this.domainClass = bundle.getDomainClass();
        this.specification = bundle.getSpecification();
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    @Override
    public Optional<T> getSingleResult() {
        try {
            T result = newBuilder()
                    .applyForSingle()
                    .build()
                    .getSingleResult();
            return Optional.ofNullable(result);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<T> getFirstResult() {
        return newBuilder()
                .applyForFirst()
                .build(0, 1)
                .getResultList()
                .stream()
                .findFirst();
    }

    @Override
    public List<T> getResultList() {
        return getResultList(newPageRequest());
    }

    @Override
    public List<T> getAllResult() {
        return newBuilder()
                .applyForList()
                .build()
                .getResultList();
    }

    @Override
    public Page<T> getResultPage() {
        // TODO 分页结果
        return null;
    }

    @Override
    public Page<T> getResultPage(int page, int size) {
        // TODO 排序条件
        PageRequest pageRequest = PageRequest.of(page, size);
        return PageableExecutionUtils.getPage(getResultList(pageRequest), pageRequest, this::count);
    }

    @Override
    public RowResult getSingleResult(Selections selections) {
        QueryAssemblerBuilder<Object> builder = newBuilder(Object.class);
        List<Column> columns = builder.resolveSelections(selections);

        TypedQuery typedQuery = builder
                .applyForSingle()
                .applySelections(columns)
                .build();
        try {
            Object value = typedQuery.getSingleResult();
            return toRowResult(columns, value);
        } catch (NoResultException e) {
            // ignore
            return RowResult.empty();
        }
    }

    @Override
    public RowResult getFirstResult(Selections selections) {
        QueryAssemblerBuilder<Object> builder = newBuilder(Object.class);
        List<Column> columns = builder.resolveSelections(selections);
        // build query
        TypedQuery<Object> typedQuery = builder
                .applyForFirst()
                .applySelections(columns)
                .build(0, 1);
        try {
            Object value = typedQuery.getSingleResult();
            return toRowResult(columns, value);
        } catch (NoResultException e) {
            return RowResult.empty();
        }
    }

    @Override
    public ResultList getResultList(Selections selections) {
        QueryAssemblerBuilder<Object> builder = newBuilder(Object.class);
        List<Column> columns = builder.resolveSelections(selections);

        TypedQuery<Object> typedQuery = builder
                .applyForList()
                .applySelections(columns)
                .buildPagingQuery();

        List<Object> values = typedQuery.getResultList();
        return toResultList(columns, values);
    }

    @Override
    public ResultList getAllResult(Selections selections) {
        QueryAssemblerBuilder<Object> builder = newBuilder(Object.class);
        List<Column> columns = builder.resolveSelections(selections);

        TypedQuery<Object> typedQuery = builder
                .applyForList()
                .applySelections(columns)
                .build();

        List<Object> values = typedQuery.getResultList();
        return toResultList(columns, values);
    }

    @Override
    public ResultPage getResultPage(int page, int size, Selections selections) {
        // TODO page result
        return null;
    }

    @Override
    public long count() {
        // TODO selection root distinct
        return count(new Selections().countRoot(false));
    }

    @Override
    public long count(String name) {
        return count(new Selections().count(name));
    }

    @Override
    public long countDistinct(String name) {
        return count(new Selections().countDistinct(name));
    }

    protected long count(Selections selections) {
        return newBuilder(Long.class)
                .applyForCount()
                .applySelections(selections)
                .build()
                .getSingleResult();
    }

    protected List<T> getResultList(Pageable pageable) {
        return newBuilder()
                .applyForList()
                .build(pageable)
                .getResultList();
    }

    protected PageRequest newPageRequest() {
        return isValidPaging()
                ? PageRequest.of(bundle.getPageNumber(), bundle.getPageSize())
                : PageRequest.of(0, 20);
    }

    protected QueryAssemblerBuilder<T> newBuilder() {
        return newBuilder(domainClass);
    }

    protected <M> QueryAssemblerBuilder<M> newBuilder(Class<M> resultClass) {
        return new QueryAssemblerBuilder<>(resultClass);
    }

    protected boolean isValidPaging() {
        return bundle.getPageNumber() >= 0 && bundle.getPageSize() > 0;
    }

    /**
     * 查询装配器
     *
     * @param <M>
     */
    public class QueryAssemblerBuilder<M> {

        private Root<T> root;
        private CriteriaQuery<M> query;
        private QueryModel queryModel;

        protected QueryAssemblerBuilder(Class<M> resultClass) {
            this.query = criteriaBuilder.createQuery(resultClass);
            this.root = query.from(bundle.getDomainClass());
            this.queryModel = new QueryModel(root, query, criteriaBuilder);
        }

        public QueryAssemblerBuilder<M> applySelections(Selections selections) {
            return applySelections(resolveSelections(selections));
        }

        public QueryAssemblerBuilder<M> applySelections(List<Column> columns) {
            List<javax.persistence.criteria.Selection> selections = columns
                    .stream()
                    .map(Column::getSelection)
                    .collect(Collectors.toList());
            if (selections.isEmpty()) {
                log.info("selections return empty selection column. {}", selections);
                return this;
            }
            if (selections.size() > 1) {
                query.multiselect(selections.toArray(new javax.persistence.criteria.Selection[0]));
            } else {
                query.select(selections.get(0));
            }
            return this;
        }

        /**
         * 解析selection, 依赖当前查询构建的root, query来解析当前的selections
         *
         * @param selections 需要查询的字段
         * @return
         */
        protected List<Column> resolveSelections(Selections selections) {
            return selections.generate(root, query, criteriaBuilder);
        }

        // quick methods

        /**
         * 构建统计查询
         */
        public QueryAssemblerBuilder<M> applyForCount() {
            return applySpecification()
                    .applyJoin()
                    .applyGrouping()
                    .applyFetch();
        }

        /**
         * 构建单结果查询
         */
        public QueryAssemblerBuilder<M> applyForSingle() {
            return applySpecification()
                    .applyFetch()
                    .applyGrouping()
                    .applyJoin();
        }

        /**
         * 构建首个结果查询
         */
        public QueryAssemblerBuilder<M> applyForFirst() {
            return applySpecification()
                    .applyFetch()
                    .applyGrouping()
                    .applyJoin()
                    .applySort();
        }

        /**
         * 构建列表结果集查询
         */
        public QueryAssemblerBuilder<M> applyForList() {
            return applySpecification()
                    .applyFetch()
                    .applyGrouping()
                    .applyJoin()
                    .applySort();
        }

        //

        /**
         * 设置查询参数
         *
         * @see QueryBundle#getSpecification()
         */
        public QueryAssemblerBuilder<M> applySpecification() {
            if (specification != null) {
                Predicate predicate = specification.toPredicate(root, query, criteriaBuilder);
                if (predicate != null) {
                    query.where(predicate);
                }
            }
            return this;
        }

        /**
         * 设置join参数
         *
         * @see QueryBundle#getJoinAttributes()
         */
        public QueryAssemblerBuilder<M> applyJoin() {
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

        /**
         * 设置fetch参数
         *
         * @see QueryBundle#getFetchAttributes()
         */
        public QueryAssemblerBuilder<M> applyFetch() {
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

        /**
         * 设置grouping参数
         *
         * @see QueryBundle#getGrouping()
         */
        public QueryAssemblerBuilder<M> applyGrouping() {
            Set<String> grouping = bundle.getGrouping();
            if (grouping != null && !grouping.isEmpty()) {
                for (String name : grouping) {
                    query.groupBy(queryModel.get(name));
                }
            }
            return this;
        }

        /**
         * 设置排序参数
         *
         * @see QueryBundle#getSort()
         */
        public QueryAssemblerBuilder<M> applySort() {
            Sort sort = bundle.getSort();
            if (sort != null) {
                query.orderBy(QueryUtils.toOrders(sort, root, criteriaBuilder));
            }
            return this;
        }

        protected boolean isAlreadyFetch(String name, JoinType joinType) {
            return root
                    .getFetches()
                    .stream()
                    .filter(e -> e.getAttribute().getName().equals(name))
                    .anyMatch(e -> e.getJoinType().equals(joinType));
        }

        protected boolean isAlreadyJoin(String name, JoinType joinType) {
            return root
                    .getJoins()
                    .stream()
                    .filter(e -> e.getAttribute().getName().equals(name))
                    .anyMatch(e -> e.getJoinType().equals(joinType));
        }

        // build result TypedQuery

        public TypedQuery<M> buildPagingQuery() {
            return build(newPageRequest());
        }

        public TypedQuery<M> build(int page, int size) {
            return build(PageRequest.of(page, size));
        }

        public TypedQuery<M> build() {
            return entityManager.createQuery(query);
        }

        public TypedQuery<M> build(Pageable pageable) {
            return entityManager
                    .createQuery(query)
                    .setFirstResult((int) pageable.getOffset())
                    .setMaxResults(pageable.getPageSize());
        }

    }

    public static ResultList toResultList(List<Column> columns, List<Object> values) {
        // @formatter:off
        List<RowResult> rows = values.stream()
                                     .map(e -> toRowResult(columns, e))
                                     .collect(Collectors.toList());
        // @formatter:on
        return new ResultList(rows);
    }

    public static RowResult toRowResult(List<Column> columns, Object value) {
        if (value == null) {
            return RowResult.empty();
        }

        Object[] valueArray = value.getClass().isArray()
                ? Object[].class.cast(value)
                : new Object[]{value};

        if (columns.size() != valueArray.length) {
            throw new QueryException("select result not match " + columns.size());
        }

        List<CellResult> cells = new ArrayList<>();
        for (int i = 0, max = valueArray.length; i < max; i++) {
            cells.add(new CellResult(i, columns.get(i), valueArray[i]));
        }
        return new RowResult(cells);
    }

}
