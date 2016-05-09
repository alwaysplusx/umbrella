package com.harmony.umbrella.plugin.log;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.harmony.umbrella.log.annotation.Logging;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public class LoggingTemplate {

    protected final Logging ann;
    protected final String templateText;

    private String expressionDelimiter = ".";

    private char delimiterStart = '{';
    private char delimiterEnd = '}';

    private List<Expression> templateExpressions;
    private Expression keyExpression;

    public LoggingTemplate(Logging ann) {
        this.ann = ann;
        this.templateText = ann.message();
        this.keyExpression = new Expression(ann.key());
    }

    public List<Expression> getTemplateExpressions() {
        if (templateExpressions == null) {
            templateExpressions = getTemplateExpressions(expressionDelimiter);
        }
        return templateExpressions;
    }

    public Expression getKeyExpression() {
        return keyExpression;
    }

    public Object getKey(ValueContext valueContext) {
        return valueContext.find(keyExpression);
    }

    public String getMessage(ValueContext valueContext) {
        return getMessage(valueContext, null);
    }

    public String getMessage(ValueContext valueContext, ObjectFormat format) {
        StringBuilder message = new StringBuilder();
        List<Expression> expressions = getTemplateExpressions();
        for (int i = 0; i < expressions.size(); i++) {
            Expression expression = expressions.get(i);
            if (expression.isText()) {
                message.append(expression.getExpressionText());
            } else {
                Object val = valueContext.find(expression);
                message.append(format == null ? String.valueOf(val) : format.format(val));
            }
        }
        return message.toString();
    }

    public String format(Object... args) {
        return format(args, null);
    }

    public String format(Object[] args, ObjectFormat format) {
        StringBuilder out = new StringBuilder();
        List<Expression> expressions = getTemplateExpressions();
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

    public List<Expression> getTemplateExpressions(String expressionDelimiter) {
        List<Expression> expressions = new ArrayList<Expression>();
        StringTokenizer st = new StringTokenizer(templateText, delimiterStart + "" + delimiterEnd);
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

    public boolean isEmpty() {
        return getTemplateExpressions().isEmpty();
    }

    public int size() {
        return getTemplateExpressions().size();
    }

    public Expression get(int index) {
        return getTemplateExpressions().get(index);
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

    public String getExpressionDelimiter() {
        return expressionDelimiter;
    }

    public Logging getLoggingAnnotation() {
        return ann;
    }

}
