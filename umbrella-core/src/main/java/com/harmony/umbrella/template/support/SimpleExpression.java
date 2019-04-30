package com.harmony.umbrella.template.support;

import com.harmony.umbrella.template.Expression;

/**
 * @author wuxii
 */
public class SimpleExpression implements Expression {

    private String text;
    private String expression;
    private boolean plainText;

    public SimpleExpression() {
    }

    public SimpleExpression(String text, String expression, boolean plainText) {
        this.text = text;
        this.expression = expression;
        this.plainText = plainText;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public String getExpression() {
        return expression;
    }

    @Override
    public boolean isPlainText() {
        return plainText;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public void setPlainText(boolean plainText) {
        this.plainText = plainText;
    }

    @Override
    public String toString() {
        return (plainText ? "text: " : "exp: ") + text;
    }

}
