package com.harmony.umbrella.data.model;

import com.harmony.umbrella.data.QueryException;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;

/**
 * 函数表达式模型
 *
 * @author wuxii
 */
public class FunctionExpressionModel implements ExpressionModel {

    private ExpressionModel parent;
    private CriteriaBuilder cb;
    private String function;

    FunctionExpressionModel(ExpressionModel parent, String function, CriteriaBuilder cb) {
        this.parent = parent;
        this.function = function;
        this.cb = cb;
    }

    @Override
    public String getName() {
        return function;
    }

    @Override
    public Expression<?> getExpression() {
        return cb.function(function, null, parent.getExpression());
    }

    @Override
    public ExpressionModel previous() {
        return parent;
    }

    @Override
    public ExpressionModel next(String name) {
        throw new QueryException("function not have next expression");
    }

    @Override
    public boolean isFunction() {
        return true;
    }

}