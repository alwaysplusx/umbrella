package com.harmony.umbrella.log.interceptor;

import com.harmony.umbrella.log.annotation.KeyExpression;
import com.harmony.umbrella.log.annotation.Logging;
import com.harmony.umbrella.log.annotation.Scope;

/**
 * @author wuxii
 */
public class ExpressionOperation {

    private String text;
    private Scope scope;
    private String bind;

    public ExpressionOperation(Logging.Expression expression) {
        this.text = expression.text();
        this.scope = expression.scope();
        this.bind = expression.bind();
    }

    public ExpressionOperation(String text, Scope scope, String bind) {
        this.text = text;
        this.scope = scope;
        this.bind = bind;
    }

    public ExpressionOperation(KeyExpression keyExpression) {
        this.text = keyExpression.text();
        this.scope = keyExpression.scope();
        this.bind = keyExpression.bind();
    }

    public String getText() {
        return text;
    }

    public Scope getScope() {
        return scope;
    }

    public String getBind() {
        return bind;
    }
}
