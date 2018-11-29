package com.harmony.umbrella.data.query.select;

import com.harmony.umbrella.data.query.Column;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;

/**
 * @author wuxii
 */
public class FunctionBuilder<X> extends SelectionBuilder<X, FunctionBuilder<X>> {

    private String functionName;

    private String name;

    public FunctionBuilder() {
    }

    public FunctionBuilder(String functionName) {
        this.functionName = functionName;
    }

    @Override
    protected Column build(QueryModel model) {
        CriteriaBuilder cb = model.getCriteriaBuilder();
        Expression expression = model.get(name);
        Expression<Object> functionExpression = cb.function(functionName, Object.class, expression);
        return new Column(name, alias, functionExpression);
    }

    public FunctionBuilder setFunctionName(String functionName) {
        this.functionName = functionName;
        return this;
    }

    public FunctionBuilder setNames(String names) {
        this.name = name;
        return this;
    }


}
