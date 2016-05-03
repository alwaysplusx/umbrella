package com.harmony.umbrella.el;

import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * @author wuxii@foxmail.com
 */
public class Expression implements Iterable<String> {

    protected final String expressionText;

    protected final String delimiter;

    public Expression(String expressionText) {
        this(expressionText, ".");
    }

    public Expression(String expressionText, String delimiter) {
        this.expressionText = expressionText;
        this.delimiter = delimiter;
    }

    public String getExpressionText() {
        return expressionText;
    }

    public String getDelimiter() {
        return delimiter;
    }

    @Override
    public Iterator<String> iterator() {
        return iterator(delimiter);
    }

    public Iterator<String> iterator(final String delimiter) {
        return new Iterator<String>() {

            StringTokenizer st = new StringTokenizer(expressionText, delimiter);

            @Override
            public String next() {
                return st.nextToken();
            }

            @Override
            public boolean hasNext() {
                return st.hasMoreTokens();
            }
        };
    }

}
