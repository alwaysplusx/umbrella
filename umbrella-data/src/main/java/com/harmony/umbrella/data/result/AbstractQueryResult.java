package com.harmony.umbrella.data.result;

import com.harmony.umbrella.data.QueryBundle;
import com.harmony.umbrella.data.QueryResult;
import com.harmony.umbrella.data.Selections;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author wuxii
 */
public abstract class AbstractQueryResult<T> implements QueryResult<T> {

    protected final QueryBundle<T> bundle;
    protected final Class<T> domainClass;
    protected Function<RowResult, T> rowConverter;

    public AbstractQueryResult(QueryBundle<T> bundle) {
        this.bundle = bundle;
        this.domainClass = bundle.getDomainClass();
    }

    protected final PageRequest newPageable() {
        return PageRequest.of(bundle.getPageNumber(), bundle.getPageSize(), bundle.getSort());
    }

    @Override
    public Optional<T> getSingleResult() {
        return Optional.ofNullable(getSingleResult(Selections.ofRoot()).toEntity(rowConverter));
    }

    @Override
    public Optional<T> getFirstResult() {
        return Optional.ofNullable(getFirstResult(Selections.ofRoot()).toEntity(rowConverter));
    }

    @Override
    public List<T> getListResult() {
        return getListResult(Selections.ofRoot()).toList(rowConverter);
    }

    @Override
    public List<T> getAllResult() {
        return getAllResult(Selections.ofRoot()).toList(rowConverter);
    }

    @Override
    public Page<T> getPageResult() {
        return getPageResult(newPageable());
    }

    @Override
    public Page<T> getPageResult(Pageable pageable) {
        return getPageResult(Selections.ofRoot(), pageable).toPage(rowConverter);
    }

}
