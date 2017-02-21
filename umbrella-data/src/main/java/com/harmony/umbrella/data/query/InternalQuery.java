package com.harmony.umbrella.data.query;

import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import com.harmony.umbrella.data.QueryFeature;
import com.harmony.umbrella.data.query.QueryBuilder.Attribute;
import com.harmony.umbrella.data.query.QueryBuilder.FetchAttributes;
import com.harmony.umbrella.data.query.QueryBuilder.JoinAttributes;
import com.harmony.umbrella.data.util.QueryUtils;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;

public class InternalQuery<T, R> {

    private static final Log log = Logs.getLog(InternalQuery.class);

    private Root<T> root;

    private CriteriaQuery<R> query;

    private CriteriaBuilder builder;

    private QueryBundle<T> bundle;

    public InternalQuery(Class<R> resultClass, CriteriaBuilder builder, QueryBundle<T> bundle) {
        this.query = builder.createQuery(resultClass);
        this.root = query.from(bundle.getEntityClass());
        this.builder = builder;
        this.bundle = bundle;
    }

    public boolean applySpecification() {
        Specification spec = bundle.getSpecification();
        Predicate predicate = spec == null ? null : spec.toPredicate(root, query, builder);
        if (predicate == null && !isAllowEmptyConditionQuery()) {
            throw new IllegalArgumentException("not allow empty condition query");
        }
        if (predicate != null) {
            query.where(predicate);
            return true;
        }
        return false;
    }

    public boolean applySort() {
        Sort sort = bundle.getPageable() != null ? bundle.getPageable().getSort() : null;
        if (sort != null) {
            query.orderBy(QueryUtils.toOrders(sort, root, builder));
            return true;
        }
        return false;
    }

    public boolean applyFetchAttributes() {
        FetchAttributes attributes = bundle.getFetchAttributes();
        if (attributes != null && !attributes.getAttributes().isEmpty()) {
            for (Attribute attr : attributes.attrs) {
                root.fetch(attr.name, attr.joniType == null ? JoinType.INNER : attr.joniType);
            }
            return true;
        }
        return false;
    }

    public boolean applyJoinAttribute() {
        JoinAttributes attributes = bundle.getJoinAttributes();
        if (attributes != null && !attributes.getAttributes().isEmpty()) {
            for (Attribute attr : attributes.attrs) {
                root.fetch(attr.name, attr.joniType == null ? JoinType.INNER : attr.joniType);
            }
            return true;
        }
        return false;
    }

    public boolean applyGrouping() {
        Set<String> grouping = bundle.getGrouping();
        if (grouping != null && !grouping.isEmpty()) {
            for (String s : grouping) {
                query.groupBy(QueryUtils.toExpressionRecursively(root, s));
            }
        }
        return false;
    }

    public CriteriaQuery<R> assembly() {
        if (!applySpecification()) {
            log.info("no predicate for query {}", query);
        }
        if (!applySort()) {
            log.debug("no sort for query {}", query);
        }
        if (!applyFetchAttributes()) {
            log.debug("no fetch attrs for query {}", query);
        }
        if (!applyJoinAttribute()) {
            log.debug("no join attrs for query {}", query);
        }
        if (!applyGrouping()) {
            log.debug("no join attrs for query {}", query);
        }
        return query;
    }

    protected boolean isAllowEmptyConditionQuery() {
        return QueryFeature.ALLOW_LIST_QUERY_WHEN_EMPTY_CONDITION.isEnable(bundle.getQueryFeature());
    }
}