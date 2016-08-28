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
        List<Expression> expressions = new ArrayList<Expression>();
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
                com.harmony.umbrella.log.annotation.Logging.Expression expAnn = findExpressionAnnotation(ann, expressionText);
                if (expAnn != null) {
                    exp = createExpression(expAnn);
                } else {
                    // 未在@Logging#expressions中找到index想对应的注解则默认创建参数index的表达式
                    exp = new Expression(expressionText, expressionDelimiter, getScope(expressionText));
                }
            }

            expressions.add(exp);
            // change current delimiter
            currentDelimiter = (isText = !isText) ? start : end;

        } // end while

        Expression keyExpression = null;

        com.harmony.umbrella.log.annotation.Logging.Expression[] keyExpressionAnn = ann.keyExpression();
        if (keyExpressionAnn.length > 0) {
            keyExpression = createExpression(keyExpressionAnn[0]);
        } else if (StringUtils.isNotBlank(ann.key())) {
            // FIXME @Logging#key() 需要适配存在'{' '}'的情况
            keyExpression = new Expression(ann.key(), this.expressionDelimiter, Scope.IN);
        }

        return new LoggingTemplate(messageTemplate, expressions, keyExpression);
    }

    protected com.harmony.umbrella.log.annotation.Logging.Expression findExpressionAnnotation(Logging ann, String expressionText) {
        com.harmony.umbrella.log.annotation.Logging.Expression result = null;
        Integer index = parse(expressionText);
        if (index != null) {
            com.harmony.umbrella.log.annotation.Logging.Expression[] expressions = ann.expressions();
            for (com.harmony.umbrella.log.annotation.Logging.Expression exp : expressions) {
                if (exp.index() == index) {
                    result = exp;
                    break;
                }
            }
            if (result == null && expressions.length > index) {
                result = expressions[index];
            }
        }
        return result;
    }

    protected Expression createExpression(com.harmony.umbrella.log.annotation.Logging.Expression expressionAnnotation) {
        // FIXME @Expression需要适配存在'{' '}'的情况
        String delimiter = expressionAnnotation.delimiter();
        if (StringUtils.isBlank(delimiter)) {
            delimiter = this.expressionDelimiter;
        }
        return new Expression(expressionAnnotation.value(), delimiter, expressionAnnotation.scope());
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

    private Integer parse(String text) {
        try {
            return Integer.valueOf(text);
        } catch (NumberFormatException e) {
            return null;
        }
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

    public class LoggingTemplate {

        private String template;
        private List<Expression> expressions;
        private Expression keyExpression;

        private LoggingTemplate(String template, List<Expression> expressions, Expression keyExpression) {
            this.template = template;
            this.keyExpression = keyExpression;
            this.expressions = expressions;
        }

        public String getTemplate() {
            return template;
        }

        public Expression[] getExpressions() {
            return expressions.toArray(new Expression[expressions.size()]);
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
