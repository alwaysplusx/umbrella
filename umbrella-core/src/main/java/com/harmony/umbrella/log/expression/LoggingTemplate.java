package com.harmony.umbrella.log.expression;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.harmony.umbrella.log.annotation.Logging;
import com.harmony.umbrella.log.annotation.Scope;
import com.harmony.umbrella.util.AntPathMatcher;
import com.harmony.umbrella.util.PathMatcher;

/**
 * 日志消息模版
 * 
 * 如: template = "记录xx的日志, 主键id={id}, 明细为={detail}". 表达式为'{', '}'中的内容
 * 
 * @author wuxii@foxmail.com
 * @see Logging#message()
 * @see Logging#key()
 */
public class LoggingTemplate {

    private PathMatcher pathMatcher = new AntPathMatcher();
    
    protected final Logging ann;
    protected final String templateText;

    private String delimiterStart = "{";
    private String delimiterEnd = "}";

    private List<Expression> expressions;
    private Expression keyExpression;

    public LoggingTemplate(Logging ann) {
        this.ann = ann;
        this.templateText = ann.message();
        this.keyExpression = new Expression(ann.key());
    }

    /**
     * 日志消息的模版
     * 
     * @see Logging#message()
     */
    public List<Expression> getExpressions() {
        if (expressions == null) {
            expressions = getExpressions(".");
        }
        return expressions;
    }

    /**
     * 日志模版中的表达式
     */
    public List<Expression> getExpressions(String expressionDelimiter) {
        List<Expression> expressions = new ArrayList<Expression>();
        StringTokenizer st = new StringTokenizer(templateText);

        boolean isText = !templateText.startsWith(delimiterStart);
        String currentDelimiter = isText ? delimiterStart : delimiterEnd;

        while (st.hasMoreTokens()) {
            String token = removeDelimiter(st.nextToken(currentDelimiter));
            currentDelimiter = (isText = !isText) ? delimiterStart : delimiterEnd;
            if (token.length() == 0) {
                continue;
            }
            Expression exp = new Expression(token, expressionDelimiter, isText, getScope(token));
            expressions.add(exp);
        }
        return expressions;
    }

    private Scope getScope(String token) {
        String[] inProperties = ann.inProperties();
        String[] outProperties = ann.outProperties();
        for (String p : outProperties) {
            if (pathMatcher.match(p, token)) {
                return Scope.OUT;
            }
        }
        for (String p : inProperties) {
            if (pathMatcher.match(p, token)) {
                return Scope.IN;
            }
        }
        return Scope.IN;
    }

    private String removeDelimiter(String token) {
        if (token.startsWith(delimiterEnd) || token.startsWith(delimiterStart)) {
            return token.substring(1);
        }
        return token;
    }

    /**
     * 日志消息的key表达式
     * 
     * @see Logging#key()
     */
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
        return valueContext.find(keyExpression);
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
        StringBuilder message = new StringBuilder();
        List<Expression> expressions = getExpressions();
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

    public String getDelimiterStart() {
        return delimiterStart;
    }

    public void setDelimiterStart(char delimiterStart) {
        this.delimiterStart = delimiterStart + "";
    }

    public String getDelimiterEnd() {
        return delimiterEnd;
    }

    public void setDelimiterEnd(char delimiterEnd) {
        this.delimiterEnd = delimiterEnd + "";
    }

    public Logging getLoggingAnnotation() {
        return ann;
    }

}
