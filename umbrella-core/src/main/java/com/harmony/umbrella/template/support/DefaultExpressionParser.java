package com.harmony.umbrella.template.support;

import com.harmony.umbrella.template.ExpressionParser;
import com.harmony.umbrella.template.Expressions;
import com.harmony.umbrella.template.ExpressionsCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.StringTokenizer;

/**
 * @author wuxii
 */
@Slf4j
public class DefaultExpressionParser implements ExpressionParser {

    private final String start;
    private final String end;
    private ExpressionsCache expressionsCache;

    public DefaultExpressionParser() {
        this(NoneExpressionsCache.INSTANCE);
    }

    public DefaultExpressionParser(ExpressionsCache expressionsCache) {
        this("${", "}", expressionsCache);
    }

    public DefaultExpressionParser(String start, String end) {
        this(start, end, NoneExpressionsCache.INSTANCE);
    }

    public DefaultExpressionParser(String start, String end, ExpressionsCache expressionsCache) {
        Assert.isTrue(StringUtils.hasText(start), "start not allow empty");
        Assert.isTrue(StringUtils.hasText(end), "end not allow empty");
        this.start = start;
        this.end = end;
        this.expressionsCache = expressionsCache;
    }

    @Override
    public Expressions parse(String text) {
        Expressions expressions = null;
        try {
            expressions = expressionsCache.getExpressionsFromCache(text);
        } catch (Exception e) {
            // TODO 日志提醒
            expressionsCache.removeExpressionsFromCache(text);
        }
        if (expressions == null) {
            expressions = doParse(text);
            expressionsCache.putExpressionsInCache(expressions);
        }
        return expressions;
    }

    protected Expressions doParse(String text) {
        SimpleExpressions expressions = new SimpleExpressions(text);

        boolean plainText = !text.startsWith(start);
        String delimiter = plainText ? start : end;
        StringTokenizer st = new StringTokenizer(text);

        while (st.hasMoreTokens()) {
            String token = st.nextToken(delimiter);
            if (token.startsWith(start)) {
                token = token.substring(start.length());
            }
            if (token.startsWith(end)) {
                token = token.substring(end.length());
            }

            SimpleExpression exp = new SimpleExpression();
            exp.setExpression(token);
            exp.setPlainText(plainText);
            exp.setText(plainText ? start + token + end : token);
            expressions.addExpression(exp);

            delimiter = (plainText = !plainText) ? start : end;
        }

        return expressions;
    }

    public void setExpressionsCache(ExpressionsCache expressionsCache) {
        this.expressionsCache = expressionsCache;
    }

}
