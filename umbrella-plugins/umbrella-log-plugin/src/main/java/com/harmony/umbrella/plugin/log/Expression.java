package com.harmony.umbrella.plugin.log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author wuxii@foxmail.com
 */
public class Expression implements Iterable<String> {

    protected final String expressionText;

    protected final String delimiter;

    private boolean isText;

    private List<String> tokens;

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
    public Iterator<String> iterator() {
        return getTokens().iterator();
    }

    public Iterator<String> iterator(final String delimiter) {
        return getTokens(delimiter).iterator();
    }

    public boolean isText() {
        return isText;
    }

    public boolean isEmpty() {
        return getTokens().isEmpty();
    }

    public int size() {
        return getTokens().size();
    }

    public String get(int index) {
        return getTokens().get(index);
    }

    private List<String> getTokens() {
        if (tokens == null) {
            tokens = getTokens(delimiter);
        }
        return tokens;
    }

    public List<String> getTokens(String delimiter) {
        List<String> result = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(expressionText, delimiter);
        while (st.hasMoreTokens()) {
            result.add(st.nextToken());
        }
        return result;
    }
}
