package com.harmony.umbrella.log.interceptor;

import com.harmony.umbrella.log.annotation.Scope;
import com.harmony.umbrella.template.Expression;

/**
 * @author wuxii
 */
public class ScopeExpression implements Expression {

    private Expression expression;
    private Scope scope;
    private String bindingExpression;

    public ScopeExpression(Scope scope, Expression expression) {
        this.expression = expression;
        this.bindingExpression = expression.getExpression();
        this.scope = scope;
    }

    public ScopeExpression(String bindingExpression, Scope scope, Expression expression) {
        this.bindingExpression = bindingExpression;
        this.scope = scope;
        this.expression = expression;
    }

    @Override
    public String getText() {
        return expression.getText();
    }

    @Override
    public String getExpression() {
        return bindingExpression;
    }

    @Override
    public boolean isPlainText() {
        return expression.isPlainText();
    }

    public Scope getScope() {
        return scope;
    }

    public Expression getOriginExpression() {
        return expression;
    }

    @Override
    public String toString() {
        return "ScopeExpression{" +
                "scope=" + scope +
                ", expression='" + bindingExpression + '\'' +
                '}';
    }
}
