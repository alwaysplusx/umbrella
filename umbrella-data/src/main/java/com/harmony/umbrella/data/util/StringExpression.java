package com.harmony.umbrella.data.util;

import java.util.StringTokenizer;

public class StringExpression {

    private String function;
    private String expression;

    public StringExpression(String function, String expression) {
        this.expression = expression;
        this.function = function;
    }

    public String getFunction() {
        return function;
    }

    public boolean isFunction() {
        return function != null;
    }

    public String getExpression() {
        return expression;
    }

    public StringTokenizer getExpressionTokenizer() {
        return new StringTokenizer(expression, ".");
    }

}