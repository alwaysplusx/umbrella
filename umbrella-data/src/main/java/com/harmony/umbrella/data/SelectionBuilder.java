package com.harmony.umbrella.data;

import com.harmony.umbrella.data.model.QueryModel;
import org.springframework.util.Assert;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;

/**
 * selection构建器
 *
 * @param <T> builder
 */
public abstract class SelectionBuilder<T extends SelectionBuilder> implements Selection {

    protected String alias;

    private final Selections selections;

    protected SelectionBuilder(Selections selections) {
        Assert.notNull(selections, "generateExpressions not allow null");
        this.selections = selections;
    }

    public T setAlias(String alias) {
        this.alias = alias;
        return (T) this;
    }

    protected abstract Expression buildSelection(QueryModel queryModel);

    @Override
    public final Column generate(Root<?> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        Expression expression = buildSelection(new QueryModel(root, query, cb));
        return new Column(alias, expression);
    }

    public final Selections and() {
        return selections;
    }

}