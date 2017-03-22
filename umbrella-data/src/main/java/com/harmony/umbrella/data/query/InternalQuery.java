package com.harmony.umbrella.data.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.harmony.umbrella.data.query.QueryBuilder.Attribute;
import com.harmony.umbrella.data.query.QueryBuilder.FetchAttributes;
import com.harmony.umbrella.data.query.QueryBuilder.JoinAttributes;
import com.harmony.umbrella.data.query.specs.FetchSpecification;
import com.harmony.umbrella.data.query.specs.GrouppingSpecification;
import com.harmony.umbrella.data.query.specs.JoinSpecification;
import com.harmony.umbrella.data.query.specs.SortSpecification;
import com.harmony.umbrella.data.util.QueryUtils;
import com.harmony.umbrella.log.Log;
import com.harmony.umbrella.log.Logs;

public class InternalQuery<T, R> {

    private static final Log log = Logs.getLog(InternalQuery.class);

    protected final Root<T> root;

    protected final CriteriaQuery<R> query;

    protected final CriteriaBuilder builder;

    protected final QueryBundle<T> bundle;

    protected InternalQuery(Class<R> resultClass, CriteriaBuilder builder, QueryBundle<T> bundle) {
        this.query = builder.createQuery(resultClass);
        this.root = query.from(bundle.getEntityClass());
        this.builder = builder;
        this.bundle = bundle;
    }

    protected boolean applySpecification() {
        Specification spec = bundle.getSpecification();
        if (spec == null || !applySpecification(spec)) {
            return false;
        }
        return true;
    }

    protected void applySort() {
        if (bundle.getSort() != null) {
            query.orderBy(QueryUtils.toOrders(bundle.getSort(), root, builder));
        }
    }

    protected boolean applyFetchAttributes() {
        FetchAttributes attributes = bundle.getFetchAttributes();
        if (attributes != null) {
            for (Attribute attr : attributes.attrs) {
                root.fetch(attr.name, attr.joniType == null ? JoinType.INNER : attr.joniType);
            }
            return true;
        }
        return false;
    }

    protected void applyJoinAttribute() {
        JoinAttributes attributes = bundle.getJoinAttributes();
        if (attributes != null) {
            for (Attribute attr : attributes.attrs) {
                root.fetch(attr.name, attr.joniType == null ? JoinType.INNER : attr.joniType);
            }
        }
    }

    protected void applyGrouping() {
        Set<String> grouping = bundle.getGrouping();
        if (grouping != null) {
            for (String s : grouping) {
                query.groupBy(QueryUtils.toExpressionRecursively(root, s));
            }
        }
    }

    protected boolean applyPaging(TypedQuery<R> typedQuery) {
        Pageable pageable = pageRequest();
        if (pageable != null) {
            typedQuery//
                    .setFirstResult(pageable.getOffset())//
                    .setMaxResults(pageable.getPageSize());
            return true;
        }
        return false;
    }

    private boolean applySpecification(Specification<T> spec) {
        Predicate predicate = spec.toPredicate(root, query, builder);
        if (predicate != null) {
            query.where(predicate);
            return true;
        }
        return false;
    }

    /**
     * 装配CriteriaQuery, 装配的内容有:
     * <ul>
     * <li>查询条件
     * <li>排序条件
     * <li>fetch
     * <li>join
     * </ul>
     * 
     * @return CriteriaQuery
     */
    protected CriteriaQuery<R> assembly(Assembly... assembly) {
        List<Assembly> assemblys = Arrays.asList(assembly);
        if (!applySpecification()) {
            log.info("no predicate for query {}", query);
        }
        if (assemblys.contains(Assembly.FETCH)) {
            applyFetchAttributes();
        }
        if (assemblys.contains(Assembly.JOIN)) {
            applyJoinAttribute();
        }
        if (assemblys.contains(Assembly.SORT)) {
            applySort();
        }
        if (assemblys.contains(Assembly.GROUP)) {
            applyGrouping();
        }
        return query;
    }

    protected void select(String... columns) {
        List<Selection> cs = new ArrayList<Selection>();
        for (String c : columns) {
            if (isFunctionExpression(c)) {
                cs.add(functionExpression(c, root));
            } else {
                cs.add(QueryUtils.toExpressionRecursively(root, c));
            }
        }
        if (cs.size() == 1) {
            query.select(cs.get(0));
        } else {
            query.multiselect(cs.toArray(new Selection[cs.size()]));
        }
    }

    protected void selectFunction(String function, String column, Class<?> resultType) {
        Expression columnExpression = QueryUtils.toExpressionRecursively(root, column);
        Expression functionExpression = builder.function(function, resultType, columnExpression);
        query.select(functionExpression);
    }

    // function expression

    private Expression functionExpression(String attributeName, Root root) {
        int left = attributeName.indexOf("(");
        int right = attributeName.indexOf(")");
        String functionName = attributeName.substring(0, left);
        String expressionName = attributeName.substring(left + 1, right);
        return builder.function(functionName, null, root.get(expressionName));
    }

    private boolean isFunctionExpression(String attributeName) {
        return attributeName.indexOf("(") > -1 && attributeName.indexOf(")") > -1;
    }

    public boolean allowFullTableQuery() {
        return QueryFeature.FULL_TABLE_QUERY.isEnable(bundle.getQueryFeature());
    }

    public boolean hasRestriction() {
        return query.getRestriction() != null;
    }

    public Pageable pageRequest() {
        int page = bundle.getPageNumber();
        int size = bundle.getPageSize();
        return page != -1 || size != -1 ? new PageRequest(bundle.getPageNumber(), bundle.getPageSize()) : null;
    }

    public Specification<T> fetchSpec() {
        return bundle.getFetchAttributes() == null ? null : new FetchSpecification<T>(bundle.getFetchAttributes().attrs);
    }

    public Specification<T> joinSpec() {
        return bundle.getJoinAttributes() == null ? null : new JoinSpecification<T>(bundle.getJoinAttributes().attrs);
    }

    public Specification<T> groupingSpec() {
        return bundle.getGrouping() == null ? null : new GrouppingSpecification<T>(bundle.getGrouping());
    }

    public Specification<T> conditonSpec() {
        return bundle.getSpecification();
    }

    public Specification<T> storSpec() {
        return bundle.getSort() == null ? null : new SortSpecification<T>(bundle.getSort());
    }

    static enum Assembly {
        CONDITION, FETCH, JOIN, GROUP, SORT
    }

}