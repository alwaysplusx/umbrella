package com.harmony.umbrella.data;

import javax.persistence.criteria.Expression;

/**
 * @author wuxii
 */
public class Column {

    private final String alias;
    private final Expression<?> expression;

    public Column(String alias, Expression<?> expression) {
        this.alias = alias;
        this.expression = expression;
    }

    public Expression<?> getExpression() {
        return expression;
    }

    public String getAlias() {
        return alias;
    }

}
