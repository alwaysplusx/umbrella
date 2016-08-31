package com.harmony.umbrella.plugin.log.expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import com.harmony.umbrella.log.annotation.Logging;
import com.harmony.umbrella.log.annotation.Logging.Scope;
import com.harmony.umbrella.util.AntPathMatcher;
import com.harmony.umbrella.util.PathMatcher;
import com.harmony.umbrella.util.StringUtils;

/**
 * @author wuxii@foxmail.com
 */
public class LoggingTemplateFactory {

    private static final String[] DEFAULT_OUT_KEY_WORDS = { "result", "exception", "response" };

    private Set<String> inKeyWords = new HashSet<String>();
    private Set<String> outKeyWords = new HashSet<String>();

    private PathMatcher pathMatcher = new AntPathMatcher();

    private Scope defaultScope = Scope.IN;

    private String start;
    private String end;
    private String expressionDelimiter;

    public LoggingTemplateFactory() {
        this('{', '}', '.', DEFAULT_OUT_KEY_WORDS);
    }

    public LoggingTemplateFactory(char start, char end, char expressionDelimiter) {
        this(start, end, expressionDelimiter, DEFAULT_OUT_KEY_WORDS);
    }

    public LoggingTemplateFactory(char start, char end, char expressionDelimiter, String... outKeyWords) {
        this.setStart(start);
        this.setEnd(end);
        this.setExpressionDelimiter(expressionDelimiter);
        this.setOutKeyWords(outKeyWords);
    }

    public LoggingTemplate getLoggingTemplate(Logging ann) {
        final List<Expression> expressions = new ArrayList<Expression>();

        final com.harmony.umbrella.log.annotation.Logging.Expression[] bindExpression = ann.expressions();
        final String messageTemplate = ann.message();

        boolean isText = !messageTemplate.startsWith(start);

        String currentDelimiter = isText ? start : end;
        StringTokenizer st = new StringTokenizer(messageTemplate);

        // 通过交替使用start/end来切割messageTemplate来组成表达式
        while (st.hasMoreTokens()) {
            String expressionText = st.nextToken(currentDelimiter);
            if (expressionText.startsWith(start) || expressionText.startsWith(end)) {
                // start or end length is 1. setter just accept char type
                expressionText = expressionText.substring(1);
            }

            // 此时添加的表达式token已经是不包含start , end的文本了
            Expression exp = null;

            // 文本表达式
            if (isText) {
                exp = new Expression(expressionText, true);
            } else {
                exp = findBindExpression(expressionText, bindExpression);
                if (exp == null) {
                    // 未在@Logging#expressions中找到index想对应的注解则默认创建参数index的表达式
                    exp = new Expression(expressionText, expressionDelimiter, getScope(expressionText));
                }
            }

            expressions.add(exp);
            // change current delimiter
            currentDelimiter = (isText = !isText) ? start : end;

        } // end while

        // parse key expression
        Expression keyExpression = null;
        com.harmony.umbrella.log.annotation.Logging.Expression[] keyExpressionAnn = ann.keyExpression();
        if (keyExpressionAnn.length > 0) {
            keyExpression = buildExpression(keyExpressionAnn[0]);
        } else if (StringUtils.isNotBlank(ann.key())) {
            String expText = trimExpression(ann.key());
            keyExpression = new Expression(expText, this.expressionDelimiter, getScope(expText));
        }

        // parse property
        List<Property> properties = new ArrayList<Property>();
        com.harmony.umbrella.log.annotation.Logging.Property[] propertiesAnn = ann.properties();
        for (com.harmony.umbrella.log.annotation.Logging.Property a : propertiesAnn) {
            Expression exp = null;
            com.harmony.umbrella.log.annotation.Logging.Expression[] propertyExpression = a.propertyExpression();
            if (propertyExpression.length > 0) {
                exp = buildExpression(propertyExpression[0]);
            }
            if (exp == null) {
                String expText = trimExpression(a.propertyValue());
                exp = new Expression(expText, expressionDelimiter, getScope(expText));
            }
            properties.add(new Property(a.propertyName(), exp));
        }

        return new LoggingTemplate(messageTemplate, expressions, properties, keyExpression);
    }

    protected Expression findBindExpression(String bind, com.harmony.umbrella.log.annotation.Logging.Expression[] expressions) {
        if (expressions.length == 0) {
            return null;
        }
        for (com.harmony.umbrella.log.annotation.Logging.Expression exp : expressions) {
            if (StringUtils.isNotBlank(exp.bind()) && exp.bind().equals(bind)) {
                return buildExpression(exp);
            }
        }
        return null;
    }

    protected Expression buildExpression(com.harmony.umbrella.log.annotation.Logging.Expression expAnn) {
        String delimiter = expAnn.delimiter();
        if (StringUtils.isBlank(delimiter)) {
            delimiter = this.expressionDelimiter;
        }
        String expText = expAnn.text();
        if (StringUtils.isBlank(expText)) {
            expText = expAnn.value();
        }
        return new Expression(trimExpression(expText), delimiter, expAnn.scope());
    }

    private String trimExpression(String text) {
        if (text.startsWith(start)) {
            text = text.substring(1, text.length());
        }
        if (text.endsWith(end)) {
            text = text.substring(0, text.length() - 1);
        }
        return text;
    }

    protected Scope getScope(String expression) {
        for (String in : inKeyWords) {
            if (pathMatcher.match(in, expression)) {
                return Scope.IN;
            }
        }
        for (String out : outKeyWords) {
            if (pathMatcher.match(out, expression)) {
                return Scope.OUT;
            }
        }
        return defaultScope;
    }

    public void setStart(char start) {
        this.start = String.valueOf(start);
    }

    public void setEnd(char end) {
        this.end = String.valueOf(end);
    }

    public void setExpressionDelimiter(char expressionDelimiter) {
        this.expressionDelimiter = String.valueOf(expressionDelimiter);
    }

    public Set<String> getInKeyWords() {
        return inKeyWords;
    }

    public void setInKeyWords(Set<String> inKeyWords) {
        this.inKeyWords = inKeyWords;
    }

    public Set<String> getOutKeyWords() {
        return outKeyWords;
    }

    public void setOutKeyWords(String[] outKeyWords) {
        this.outKeyWords.clear();
        Collections.addAll(this.outKeyWords, outKeyWords);
    }

    public void setOutKeyWords(Set<String> outKeyWords) {
        this.outKeyWords = outKeyWords;
    }

    public static final class LoggingTemplate {

        private String template;
        private List<Expression> expressions;
        private List<Property> properties;
        private Expression keyExpression;

        private LoggingTemplate(String template, List<Expression> expressions, List<Property> properties, Expression keyExpression) {
            this.template = template;
            this.keyExpression = keyExpression;
            this.expressions = expressions;
            this.properties = properties;
        }

        public String getTemplate() {
            return template;
        }

        public Expression[] getExpressions() {
            return expressions.toArray(new Expression[0]);
        }

        public Property[] getProperties() {
            return properties.toArray(new Property[0]);
        }

        public Expression getKeyExpression() {
            return keyExpression;
        }

        /**
         * 通过key expression 获取值
         * 
         * @param valueContext
         *            日志消息的内容
         * @return
         */
        public Object getId(ValueContext valueContext) {
            return keyExpression == null ? null : valueContext.find(keyExpression);
        }

        /**
         * 通过template expression 获取消息内容
         * 
         * @param valueContext
         *            日志消息的内容
         * @return
         */
        public String getMessage(ValueContext valueContext) {
            return getMessage(valueContext, null);
        }

        /**
         * 通过template expression 获取消息内容
         * 
         * @param valueContext
         *            日志消息内容
         * @param format
         *            内容格式化工具
         * @return
         */
        public String getMessage(ValueContext valueContext, ObjectFormat format) {
            StringBuilder out = new StringBuilder();
            for (Expression exp : expressions) {
                if (exp.isText()) {
                    out.append(exp.getExpressionText());
                } else {
                    Object val = valueContext.find(exp);
                    out.append(format == null ? String.valueOf(val) : format.format(val));
                }
            }
            return out.toString();
        }

        /**
         * 通过template expression 获取消息内容
         */
        public String format(Object... args) {
            return format(args, null);
        }

        /**
         * 通过template expression 获取消息内容
         */
        public String format(Object[] args, ObjectFormat format) {
            StringBuilder out = new StringBuilder();
            for (int i = 0; i < expressions.size(); i++) {
                Expression expression = expressions.get(i);
                if (expression.isText()) {
                    out.append(expression.getExpressionText());
                } else if (i >= args.length) {
                    out.append("").append(expression.getExpressionText()).append("");
                } else {
                    out.append(format == null ? String.valueOf(args[i]) : format.format(args[i]));
                }
            }
            return out.toString();
        }
    }
}
