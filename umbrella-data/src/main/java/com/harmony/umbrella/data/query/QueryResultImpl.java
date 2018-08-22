package com.harmony.umbrella.data.query;

import com.harmony.umbrella.data.model.RootModel;
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
                    .applyGrouping()
                    .applySpecification()
                    .applySort()
                    .applyJoin()
                    .applyFetch()
                    .build()
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public T getFirstResult() {
        return newAssembler()
                .applyAll()
                .build(0, 1)
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<T> getResultList() {
        return newAssembler()
                .applyAll()
                .build(true)
                .getResultList();
    }

    @Override
    public List<T> getAllResult() {
        return newAssembler()
                .applyAll()
                .build()
                .getResultList();
    }

    @Override
    public Page<T> getResultPage() {
        return PageableExecutionUtils.getPage(getResultList(), newPageRequest(), () -> count());
    }

    @Override
    public <E> E getSingleResult(Selections<T> selections, Class<E> resultClass) {
        try {
            return newAssembler(resultClass)
                    .applyGrouping()
                    .applySpecification()
                    .applySort()
                    .applyJoin()
                    .applyFetch()
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
                .applyAll()
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
                .applyAll()
                .applySelections(selections)
                .build(true)
                .getResultList();
    }

    @Override
    public <E> List<E> getAllResult(Selections<T> selections, Class<E> resultClass) {
        return newAssembler(resultClass)
                .applyAll()
                .applySelections(selections)
                .build()
                .getResultList();
    }

    @Override
    public <E> Page<E> getResultPage(String countName, Selections<T> selections, Class<E> resultClass) {
        return PageableExecutionUtils.getPage(getResultList(selections, resultClass), newPageRequest(), () -> count(countName));
    }

    @Override
    public <E> Page<E> getDistinctResultPage(String countName, Selections<T> selections, Class<E> resultClass) {
        return PageableExecutionUtils.getPage(getResultList(selections, resultClass), newPageRequest(), () -> countDistinct(countName));
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
                .applySpecification()
                .applyGrouping()
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

        public QueryAssembler<M> applySelections(Selections<T> selections) {
            List<Selection> columns = selections.select(root, query, builder);
            if (columns.isEmpty()) {
                throw new QueryException();
            }
            if (columns.size() > 1) {
                query.multiselect(columns.toArray(new Selection[columns.size()]));
            } else {
                query.select(columns.get(0));
            }
            return this;
        }

        public QueryAssembler<M> applyAll() {
            return applySpecification()
                    .applyFetch()
                    .applyGrouping()
                    .applyJoin()
                    .applySort();
        }


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

        protected boolean isAlreadyJoin(String name, JoinType joinType) {
            return root
                    .getJoins()
                    .stream()
                    .filter(e -> e.getAttribute().getName().equals(name))
                    .anyMatch(e -> e.getJoinType().equals(joinType));
        }

        public TypedQuery<M> build(boolean paging) {
            return paging ? build(newPageRequest()) : build();
        }


        public TypedQuery<M> buildPagingQuery() {
            return build(true);
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
