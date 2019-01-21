package com.harmony.umbrella.data.result;

import com.harmony.umbrella.data.QueryBundle;
import com.harmony.umbrella.data.QueryResult;
import com.harmony.umbrella.data.Selections;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author wuxii
 */
public abstract class AbstractQueryResult<T> implements QueryResult<T> {

    protected final QueryBundle<T> bundle;
    protected final Class<T> domainClass;

    public AbstractQueryResult(QueryBundle<T> bundle) {
        this.bundle = bundle;
        this.domainClass = bundle.getDomainClass();
    }

    protected final PageRequest newPageable() {
        return PageRequest.of(bundle.getPageNumber(), bundle.getPageSize(), bundle.getSort());
    }

    @Override
    public T getSingleResult() {
        return getSingleResult(Selections.ofRoot()).toEntity(domainClass);
    }

    @Override
    public T getFirstResult() {
        return getFirstResult(Selections.ofRoot()).toEntity(domainClass);
    }

    @Override
    public List<T> getListResult() {
        return getListResult(Selections.ofRoot()).toList(domainClass);
    }

    @Override
    public List<T> getAllResult() {
        return getAllResult(Selections.ofRoot()).toList(domainClass);
    }

    @Override
    public Page<T> getPageResult() {
        return getPageResult(newPageable());
    }

    @Override
    public Page<T> getPageResult(Pageable pageable) {
        return getPageResult(Selections.ofRoot(), pageable).toPage(domainClass);
    }

}
