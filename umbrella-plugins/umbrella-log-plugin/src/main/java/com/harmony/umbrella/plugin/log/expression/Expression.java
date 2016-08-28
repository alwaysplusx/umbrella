package com.harmony.umbrella.plugin.log.expression;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import com.harmony.umbrella.log.annotation.Logging.Scope;

/**
 * 日志消息模版中的表达式
 * 
 * 如: a.b.c.name为一个表达式, 分割符号为 '.'
 * 
 * @author wuxii@foxmail.com
 */
public class Expression implements Iterable<String> {

    private String expressionText;

    private String delimiter;

    private boolean isText;

    private List<String> tokens;

    private Scope scope = Scope.IN;

    Expression() {
    }

    Expression(String expressionText, boolean isText) {
        this.expressionText = expressionText;
        this.isText = isText;
    }

    Expression(String expressionText, String delimiter, Scope scope) {
        this.expressionText = expressionText;
        this.delimiter = delimiter;
        this.isText = false;
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
        return getTokenList().iterator();
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

    public boolean isScopeOf(Scope scope) {
        return this.scope == scope;
    }

    public boolean hasTokens() {
        return getTokens().length > 0;
    }

    public String[] getTokens() {
        List<String> list = getTokenList();
        return list.toArray(new String[list.size()]);
    }

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    private List<String> getTokenList() {
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
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((delimiter == null) ? 0 : delimiter.hashCode());
        result = prime * result + ((expressionText == null) ? 0 : expressionText.hashCode());
        result = prime * result + (isText ? 1231 : 1237);
        result = prime * result + ((scope == null) ? 0 : scope.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Expression other = (Expression) obj;
        if (delimiter == null) {
            if (other.delimiter != null)
                return false;
        } else if (!delimiter.equals(other.delimiter))
            return false;
        if (expressionText == null) {
            if (other.expressionText != null)
                return false;
        } else if (!expressionText.equals(other.expressionText))
            return false;
        if (isText != other.isText)
            return false;
        if (scope != other.scope)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "expression {expression:" + expressionText + ", text:" + isText + "}";
    }
}
