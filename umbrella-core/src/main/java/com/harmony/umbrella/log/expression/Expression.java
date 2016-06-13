package com.harmony.umbrella.log.expression;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import com.harmony.umbrella.log.annotation.Scope;

/**
 * 日志消息模版中的表达式
 * 
 * 如: a.b.c.name为一个表达式, 分割符号为 '.'
 * 
 * @author wuxii@foxmail.com
 */
public class Expression implements Iterable<String> {

    protected final String expressionText;

    protected final String delimiter;

    private boolean isText;

    private List<String> tokens;

    private Scope scope;

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

    public Expression(String expressionText, String delimiter, boolean isText, Scope scope) {
        this.expressionText = expressionText;
        this.delimiter = delimiter;
        this.isText = isText;
        this.scope = scope;
    }

    /**
     * 表达式文本内容
     */
    public String getExpressionText() {
        return expressionText;
    }

    /**
     * 表达式的分隔符
     */
    public String getDelimiter() {
        return delimiter;
    }

    /**
     * 迭代表达式
     */
    @Override
    public Iterator<String> iterator() {
        return getTokens().iterator();
    }

    /**
     * 通过分隔符号分割表达式
     * 
     * @param delimiter
     *            分割符
     * @return
     */
    public Iterator<String> iterator(final String delimiter) {
        return getTokens(delimiter).iterator();
    }

    /**
     * 判断exception是否是文本,不需要解析
     */
    public boolean isText() {
        return isText;
    }

    /**
     * 表达式中是否有内容
     */
    public boolean isEmpty() {
        return getTokens().isEmpty();
    }

    public boolean isScopeOf(Scope scope) {
        return this.scope == scope;
    }

    /**
     * 表达式有多少内容
     */
    public int size() {
        return getTokens().size();
    }

    /**
     * 获取第index个几点的表达式内容
     */
    public String get(int index) {
        return getTokens().get(index);
    }

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
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

    @Override
    public String toString() {
        return "expression {text:" + expressionText + ", isText:" + isText + "}";
    }
}
