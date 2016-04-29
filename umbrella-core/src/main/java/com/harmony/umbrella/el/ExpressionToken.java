package com.harmony.umbrella.el;

/**
 * @author wuxii@foxmail.com
 */
public class ExpressionToken {

    private Expression expression;

    private String token;
    private int startIndex;
    private int endIndex;

    public ExpressionToken(Expression expression, String token, int startIndex, int endIndex) {
        this.expression = expression;
        this.token = token;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    protected ExpressionToken(String token, int startIndex, int endIndex) {
        this.token = token;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public String getToken() {
        return token;
    }

    public String getFullText() {
        return expression.getText();
    }

    public Expression getExpression() {
        return expression;
    }

    void setExpression(Expression expression) {
        this.expression = expression;
    }

}
