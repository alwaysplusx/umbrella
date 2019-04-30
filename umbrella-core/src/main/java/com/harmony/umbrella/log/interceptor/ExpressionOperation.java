package com.harmony.umbrella.log.interceptor;

import com.harmony.umbrella.log.annotation.Scope;

/**
 * @author wuxii
 */
public class ExpressionOperation {

    private String text;
    private Scope scope;

    public ExpressionOperation(String text, Scope scope) {
        this.text = text;
        this.scope = scope;
    }

    public String getText() {
        return text;
    }

    public Scope getScope() {
        return scope;
    }

}
