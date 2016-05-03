package com.harmony.umbrella.el;

import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * @author wuxii@foxmail.com
 */
public class Expression implements Iterable<Expression> {

    protected final String expressionText;

    protected final String delimiter;

    private boolean isText;

    public Expression(String expressionText) {
        this(expressionText, ".");
    }

    public Expression(String expressionText, String delimiter) {
        this.expressionText = expressionText;
        this.delimiter = delimiter;
    }

    public Expression(String expressionText, String delimiter, boolean isText) {
        this.expressionText = expressionText;
        this.delimiter = delimiter;
        this.isText = isText;
    }

    public String getExpressionText() {
        return expressionText;
    }

    public String getDelimiter() {
        return delimiter;
    }

    @Override
    public Iterator<Expression> iterator() {
        return iterator(delimiter);
    }

    public Iterator<Expression> iterator(final String delimiter) {
        return new Iterator<Expression>() {

            StringTokenizer st = new StringTokenizer(expressionText, delimiter);

            @Override
            public Expression next() {
                return new Expression(st.nextToken(), delimiter, isText);
            }

            @Override
            public boolean hasNext() {
                return st.hasMoreTokens();
            }
        };
    }

    public boolean isText() {
        return isText;
    }

}
