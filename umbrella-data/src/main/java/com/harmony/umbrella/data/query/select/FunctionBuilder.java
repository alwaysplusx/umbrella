package com.harmony.umbrella.data.query.select;

import com.harmony.umbrella.data.query.Column;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

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
    public Column generate(Root<X> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return null;
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
