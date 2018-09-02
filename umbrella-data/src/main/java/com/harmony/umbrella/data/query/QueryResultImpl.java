package com.harmony.umbrella.data.query;

import com.harmony.umbrella.data.model.ExpressionModel;
import com.harmony.umbrella.data.model.RootModel;
import com.harmony.umbrella.data.result.SelectionAndResult;
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
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.harmony.umbrella.data.util.QueryUtils.toSelectionAndResult;

/**
 * @author wuxii@foxmail.com
 */
public class QueryResultImpl<T> implements QueryResult<T> {

    private QueryBundle<T> bundle;
    private EntityManager entityManager;
    private CriteriaBuilder builder;
    private Class<T> domainClass;
    private Specification<T> specification;

    public QueryResultImpl(EntityManager entityManager, QueryBundle<T> bundle) {
        this.bundle = bundle;
        this.domainClass = bundle.getDomainClass();
        this.specification = bundle.getSpecification();
        this.entityManager = entityManager;
        this.builder = entityManager.getCriteriaBuilder();
    }

    @Override
    public T getSingleResult() {
        try {
            return newAssembler()
                    .applyForSingle()
                    .build()
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public T getFirstResult() {
        return newAssembler()
                .applyForFirst()
                .build(0, 1)
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<T> getResultList() {
        return getResultList(newPageRequest());
    }

    @Override
    public List<T> getAllResult() {
        return newAssembler()
                .applyForList()
                .build()
                .getResultList();
    }

    @Override
    public Page<T> getResultPage() {
        PageRequest pageRequest = newPageRequest();
        return PageableExecutionUtils.getPage(getResultList(pageRequest), pageRequest, () -> count());
    }

    protected List<T> getResultList(Pageable pageable) {
        return newAssembler()
                .applyForList()
                .build(pageable)
                .getResultList();
    }

    @Override
    public <E> E getSingleResult(Selections<T> selections, Class<E> resultClass) {
        try {
            return newAssembler(resultClass)
                    .applyForSingle()
                    .applySelections(selections)
                    .build()
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public <E> E getFirstResult(Selections<T> selections, Class<E> resultClass) {
        return newAssembler(resultClass)
                .applyForFirst()
                .applySelections(selections)
                .build(0, 1)
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public <E> List<E> getResultList(Selections<T> selections, Class<E> resultClass) {
        return newAssembler(resultClass)
                .applyForList()
                .applySelections(selections)
                .buildPagingQuery()
                .getResultList();
    }

    @Override
    public <E> List<E> getAllResult(Selections<T> selections, Class<E> resultClass) {
        return newAssembler(resultClass)
                .applyForList()
                .applySelections(selections)
                .build()
                .getResultList();
    }

    @Override
    public <E> E getSingleResult(Selections<T> selections, Function<SelectionAndResult, E> converter) {
        QueryAssembler<Object> assembler = newAssembler(Object.class);
        List<Selection> columns = assembler.toSelectionList(selections);
        try {
            Object result = assembler
                    .applyForSingle()
                    .applySelections(columns)
                    .build()
                    .getSingleResult();
            return result == null ? null : converter.apply(toSelectionAndResult(columns, result));
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public <E> E getFirstResult(Selections<T> selections, Function<SelectionAndResult, E> converter) {
        QueryAssembler<Object> assembler = newAssembler(Object.class);
        List<Selection> columns = assembler.toSelectionList(selections);
        return assembler
                .applyForFirst()
                .applySelections(columns)
                .build(0, 1)
                .getResultList()
                .stream()
                .findFirst()
                .map(e -> toSelectionAndResult(columns, e))
                .map(converter)
                .orElse(null);
    }

    @Override
    public <E> List<E> getResultList(Selections<T> selections, Function<SelectionAndResult, E> converter) {
        QueryAssembler<Object> assembler = newAssembler(Object.class);
        List<Selection> columns = assembler.toSelectionList(selections);
        return assembler
                .applyForList()
                .applySelections(columns)
                .buildPagingQuery()
                .getResultList()
                .stream()
                .map(e -> toSelectionAndResult(columns, e))
                .map(converter)
                .collect(Collectors.toList());
    }

    @Override
    public <E> List<E> getAllResult(Selections<T> selections, Function<SelectionAndResult, E> converter) {
        QueryAssembler<Object> assembler = newAssembler(Object.class);
        List<Selection> columns = assembler.toSelectionList(selections);
        return assembler
                .applyForList()
                .applySelections(columns)
                .build()
                .getResultList()
                .stream()
                .map(e -> toSelectionAndResult(columns, e))
                .map(converter)
                .collect(Collectors.toList());
    }

    @Override
    public long count() {
        return count(Selections.count());
    }

    @Override
    public long count(String countName) {
        return count(Selections.count(countName));
    }

    @Override
    public long countDistinct(String countName) {
        return count(Selections.countDistinct(countName));
    }

    protected long count(Selections<T> selections) {
        return newAssembler(Long.class)
                .applyForCount()
                .applySelections(selections)
                .build()
                .getSingleResult();
    }

    protected PageRequest newPageRequest() {
        return isValidPaging()
                ? PageRequest.of(bundle.getPageNumber(), bundle.getPageSize())
                : PageRequest.of(0, 20);
    }

    protected QueryAssembler<T> newAssembler() {
        return newAssembler(domainClass);
    }

    protected <M> QueryAssembler<M> newAssembler(Class<M> resultClass) {
        return new QueryAssembler<>(resultClass);
    }

    protected boolean isValidPaging() {
        return bundle.getPageNumber() >= 0 && bundle.getPageSize() > 0;
    }

    public class QueryAssembler<M> {

        private Root<T> root;
        private RootModel rootModel;
        private CriteriaQuery<M> query;

        protected QueryAssembler(Class<M> resultClass) {
            this.query = builder.createQuery(resultClass);
            this.root = query.from(bundle.getDomainClass());
            this.rootModel = RootModel.of(root, builder);
        }

        public QueryAssembler<M> applySelections(final List<Selection> selections) {
            Selection[] cleanSelections = toCleanSelections(selections);
            if (cleanSelections.length > 1) {
                query.multiselect(cleanSelections);
            } else {
                query.select(cleanSelections[0]);
            }
            return this;
        }

        private Selection[] toCleanSelections(List<Selection> selections) {
            Selection[] result = new Selection[selections.size()];
            for (int i = 0, max = selections.size(); i < max; i++) {
                Selection selection = selections.get(i);
                result[i] = selection instanceof ExpressionModel ? ((ExpressionModel) selection).toExpression() : selection;
            }
            return result;
        }

        public QueryAssembler<M> applySelections(Selections<T> selections) {
            return applySelections(toSelectionList(selections));
        }

        // quick methods

        public QueryAssembler<M> applyForCount() {
            return applySpecification()
                    .applyJoin()
                    .applyGrouping()
                    .applyFetch();
        }

        public QueryAssembler<M> applyForSingle() {
            return applySpecification()
                    .applyFetch()
                    .applyGrouping()
                    .applyJoin();
        }

        public QueryAssembler<M> applyForFirst() {
            return applySpecification()
                    .applyFetch()
                    .applyGrouping()
                    .applyJoin()
                    .applySort();
        }

        public QueryAssembler<M> applyForList() {
            return applySpecification()
                    .applyFetch()
                    .applyGrouping()
                    .applyJoin()
                    .applySort();
        }

        //

        public QueryAssembler<M> applySpecification() {
            if (specification != null) {
                Predicate predicate = specification.toPredicate(root, query, builder);
                if (predicate != null) {
                    query.where(predicate);
                }
            }
            return this;
        }

        public QueryAssembler<M> applyJoin() {
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

        public QueryAssembler<M> applyFetch() {
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

        public QueryAssembler<M> applyGrouping() {
            Set<String> grouping = bundle.getGrouping();
            if (grouping != null && !grouping.isEmpty()) {
                for (String name : grouping) {
                    query.groupBy(rootModel.get(name).toExpression());
                }
            }
            return this;
        }

        public QueryAssembler<M> applySort() {
            Sort sort = bundle.getSort();
            if (sort != null) {
                query.orderBy(QueryUtils.toOrders(sort, root, builder));
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

        public List<Selection> toSelectionList(Selections<T> selections) {
            List<Selection> columns = selections.select(root, query, builder);
            if (columns.isEmpty()) {
                throw new QueryException("not selection column found");
            }
            return columns;
        }

        protected boolean isAlreadyJoin(String name, JoinType joinType) {
            return root
                    .getJoins()
                    .stream()
                    .filter(e -> e.getAttribute().getName().equals(name))
                    .anyMatch(e -> e.getJoinType().equals(joinType));
        }

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

}
