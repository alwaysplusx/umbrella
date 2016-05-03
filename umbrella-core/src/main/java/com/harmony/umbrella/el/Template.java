package com.harmony.umbrella.el;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public class Template implements Iterable<Expression> {

    public static final char DELIM_START = '{';

    public static final char DELIM_END = '}';

    public static final String EXPRESSION_DELIMITER = ".";

    protected final String templateText;
    private char delimiterStart;
    private char delimiterEnd;

    private List<Expression> expressions;

    public Template(String templateText) {
        this(templateText, DELIM_START, DELIM_END);
    }

    public Template(String templateText, char delimiterStart, char delimiterEnd) {
        this.templateText = templateText;
        this.delimiterStart = delimiterStart;
        this.delimiterEnd = delimiterEnd;
    }

    @Override
    public Iterator<Expression> iterator() {
        return getExpressions().iterator();
    }

    private List<Expression> getExpressions() {
        if (expressions == null) {
            expressions = getExpressions(EXPRESSION_DELIMITER);
        }
        return expressions;
    }

    public List<Expression> getExpressions(String expressionDelimiter) {
        List<Expression> expressions = new ArrayList<Expression>();
        StringTokenizer st = new StringTokenizer(templateText);

        int index = 0;
        boolean isText = true;
        String currentDelimiter = delimiterStart + "";

        while (st.hasMoreTokens()) {
            String token = st.nextToken(currentDelimiter);
            if (index > 0 && StringUtils.isNotBlank(token)) {
                token = token.substring(1);
            }
            expressions.add(new Expression(token, expressionDelimiter, isText));
            currentDelimiter = ((isText = !isText) ? delimiterStart : delimiterEnd) + "";
            index++;
        }

        return expressions;
    }

    public String format(Object... args) {
        return format(args, null);
    }

    public String format(Object[] args, ObjectFormat format) {
        StringBuilder out = new StringBuilder();
        List<Expression> expressions = getExpressions();
        for (int i = 0; i < expressions.size(); i++) {
            Expression expression = expressions.get(i);
            if (expression.isText()) {
                out.append(expression.getExpressionText());
            } else if (i >= args.length) {
                out.append(delimiterStart).append(expression.getExpressionText()).append(delimiterEnd);
            } else {
                out.append(format == null ? String.valueOf(args[i]) : format.format(args[i]));
            }
        }
        return out.toString();
    }

    public boolean isEmpty() {
        return getExpressions().isEmpty();
    }

    public int size() {
        return getExpressions().size();
    }

    public Expression get(int index) {
        return getExpressions().get(index);
    }

    public String getTemplateText() {
        return templateText;
    }

    public char getDelimiterStart() {
        return delimiterStart;
    }

    public char getDelimiterEnd() {
        return delimiterEnd;
    }

}
