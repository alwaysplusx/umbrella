package com.harmony.umbrella.data.query;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import com.harmony.umbrella.data.query.QueryBuilder.FetchAttributes;
import com.harmony.umbrella.data.query.QueryBuilder.JoinAttributes;

/**
 * @author wuxii@foxmail.com
 */
public class QueryBundleImpl<M> implements QueryBundle<M>, Serializable {

    private static final long serialVersionUID = 1L;

    Class<M> entityClass;

    Specification condition;

    FetchAttributes fetchAttributes;

    JoinAttributes joinAttributes;

    Set<String> grouping;

    int queryFeature;

    int pageNumber;

    int pageSize;

    Sort sort;

    public QueryBundleImpl() {
    }

    public QueryBundleImpl(QueryBundle bundle) {
        this.entityClass = bundle.getDomainClass();
        this.condition = bundle.getSpecification();
        this.fetchAttributes = bundle.getFetchAttributes();
        this.joinAttributes = bundle.getJoinAttributes();
        this.grouping = bundle.getGrouping();
        this.queryFeature = bundle.getQueryFeature();
        this.pageNumber = bundle.getPageNumber();
        this.pageSize = bundle.getPageSize();
        this.sort = bundle.getSort();
    }

    @Override
    public Class<M> getDomainClass() {
        return entityClass;
    }

    @Override
    public int getPageNumber() {
        return pageNumber;
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Specification getSpecification() {
        return condition;
    }

    @Override
    public int getQueryFeature() {
        return queryFeature;
    }

    @Override
    public FetchAttributes getFetchAttributes() {
        return fetchAttributes;
    }

    @Override
    public JoinAttributes getJoinAttributes() {
        return joinAttributes;
    }

    @Override
    public Set<String> getGrouping() {
        return grouping;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append("select * from ").append(entityClass != null ? entityClass.getSimpleName() : "UnknowType");
        if (condition != null) {
            out.append(" where ").append(condition);
        }
        if (grouping != null && !grouping.isEmpty()) {
            out.append(" group by ");
            Iterator<String> it = grouping.iterator();
            while (it.hasNext()) {
                out.append(it.next());
                if (it.hasNext()) {
                    out.append(", ");
                }
            }
        }
        return out.toString();
    }

}
