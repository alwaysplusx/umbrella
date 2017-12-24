package com.harmony.umbrella.log.template;

import com.harmony.umbrella.log.annotation.Logging.Scope;

/**
 * 定义了token的取值scope
 * 
 * @author wuxii@foxmail.com
 * @see Token
 */
public class ScopeToken {

    private Scope scope;
    private Token token;

    public ScopeToken(Scope scope, String token) {
        if (token == null) {
            throw new IllegalArgumentException("token must not null");
        }
        this.scope = scope;
        this.token = new Token(token, false);
    }

    public ScopeToken(Scope scope, Token token) {
        if (token == null) {
            throw new IllegalArgumentException("token must not null");
        }
        this.scope = scope;
        this.token = token;
    }

    public Scope getScope() {
        return scope;
    }

    public Token getToken() {
        return token;
    }

    public String getTokenString() {
        return token.getToken();
    }

    public boolean isPlainText() {
        return token.isPlainText();
    }

}
