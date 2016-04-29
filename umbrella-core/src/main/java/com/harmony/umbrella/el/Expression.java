package com.harmony.umbrella.el;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author wuxii@foxmail.com
 */
public class Expression implements Iterable<ExpressionToken> {

    private String text;

    private List<ExpressionToken> tokens = new ArrayList<ExpressionToken>();

    private char delimStart;
    private char delimEnd;
    private char escape;

    private Expression(String text, char delimStart, char delimEnd, char escape) {
        this.text = text;
        this.delimStart = delimStart;
        this.delimEnd = delimEnd;
        this.escape = escape;
    }

    @Override
    public Iterator<ExpressionToken> iterator() {
        return tokens.iterator();
    }

    public String getText() {
        return text;
    }

    public char getDelimStart() {
        return delimStart;
    }

    public char getDelimEnd() {
        return delimEnd;
    }

    public char getEscape() {
        return escape;
    }

    public ExpressionToken get(int index) {
        return tokens.get(index);
    }

    public boolean isEmpty() {
        return tokens.isEmpty();
    }

    public int size() {
        return tokens.size();
    }

    void setExpressionToken(List<ExpressionToken> tokens) {
        for (ExpressionToken token : tokens) {
            token.setExpression(this);
        }
        this.tokens = tokens;
    }

    private void addToken(ExpressionToken token) {
        token.setExpression(this);
        tokens.add(token);
    }

    public static final char DELIM_START = '{';

    public static final char DELIM_END = '}';

    public static final char ESCAPE_CHAR = '\\';

    public static Expression parse(String text) {
        return parse(text, DELIM_START, DELIM_END, ESCAPE_CHAR);
    }

    public static Expression parse(String text, char delimStart, char delimEnd, char escape) {
        Expression expression = new Expression(text, delimStart, delimEnd, escape);
        int startIndex = text.indexOf(delimStart);
        for (int i = startIndex + 1, max = text.length(); i < max; i++) {
            char curChar = text.charAt(i);
            if (curChar == delimEnd) {
                String token = text.substring(startIndex, i + 1);
                expression.addToken(new ExpressionToken(expression, token, startIndex, i));
            } else if (curChar == delimStart) {
                startIndex = i;
            }
        }
        return expression;
    }

    public static void main(String[] args) {
        Expression expression = parse("对数据Sample[{0.sampleId}]进行保存操作，操作结果为：{result}");
        for (ExpressionToken token : expression) {
            System.out.println(token.getToken());
        }
    }
}
